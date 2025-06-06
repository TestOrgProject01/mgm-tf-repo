package com.mgmresorts.dataapi.performance.cdpapi;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;
import us.abstracta.jmeter.javadsl.azure.AzureEngine;
import us.abstracta.jmeter.javadsl.core.DslTestPlan;
import us.abstracta.jmeter.javadsl.core.DslTestPlan.TestPlanChild;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import us.abstracta.jmeter.javadsl.core.stats.CountMetricSummary;
import us.abstracta.jmeter.javadsl.core.stats.StatsSummary;
import us.abstracta.jmeter.javadsl.core.stats.TimeMetricSummary;
import us.abstracta.jmeter.javadsl.core.threadgroups.BaseThreadGroup.ThreadGroupChild;
import us.abstracta.jmeter.javadsl.util.TestResource;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static com.mgmresorts.dataapi.performance.cdpapi.Constants.KEY_AUTH_TOKEN;
import static com.mgmresorts.dataapi.performance.cdpapi.TestHelper.logBanner;
import static java.util.Locale.US;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
import static us.abstracta.jmeter.javadsl.dashboard.DashboardVisualizer.dashboardVisualizer;
import static us.abstracta.jmeter.javadsl.http.DslCookieManager.CookiePolicy.STANDARD;

public abstract class BaseTestPlanLibrary extends BasePerfLibrary {
	@SuppressWarnings("java:S2885")
	private static final DateFormat LONG_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static final NumberFormat MILLIS_FORMAT = NumberFormat.getNumberInstance(US);
	private static final NumberFormat AVG_FORMATTER = new DecimalFormat("#0.00");

	protected static final String ENV_TESTS = "pref.tests";

	private static final String JMETER_RUN_ON_AZURE = "jmeter.runOnAzure";
	private static final String JMETER_SHOW_GUI = "jmeter.showGui";
	private static final String JMETER_JTL_ENABLED = "jmeter.jtlWriter.enabled";
	private static final String JMETER_JTL_PATH = "jmeter.jtlWriter.path";
	private static final String JTL_EXT = ".jtl";
	private static final String JMETER_HTML_ENABLED = "jmeter.htmlReport.enabled";
	private static final String JMETER_HTML_PATH = "jmeter.htmlReport.path";

	private static final String CLIENT_ID = ".clientId";
	private static final String CLIENT_SECRET = ".clientSecret";
	private static final String TEST_DATA = ".testData";
	private static final String THREADS = ".threads";
	private static final String ITERATIONS = ".iterations";
	private static final String RANDOM_TEST_DATA = ".randomTestData";
	private static final String VAR_NAME = ".varName";
	private static final String POST_ITER_MAX_DELAY_MS = ".postIterationMaxDelayMs";
	private static final String RAMP_UP_SEC = ".rampUpSec";
	private static final String INITIAL_DELAY_MS = ".initialDelayMs";

	static {
		MILLIS_FORMAT.setGroupingUsed(true);
	}

	public interface AddThreadGroupChildren {
		List<ThreadGroupChild> add(TestLibrary lib);
	}

	/**
	 * create a test plan with a single thread group.
	 * after a test plan is created, this method will proceed to run it.
	 * <p>
	 * supported configurations:<ul>
	 * <li>{TestName}.threads, default to 1</li>
	 * <li>{TestName}.iterations, default to 1</li>
	 * <li>{TestName}.testData for CSV test data file</li>
	 * <li>{TestName}.randomTestData for random CSV test access, default to true</li>
	 * <li>{TestName}.clientId for OKTA authentication/JWT</li>
	 * <li>{TestName}.clientSecret for OKTA authentication/JWT</li>
	 * <li>{TestName}.varName to capture generated JWT</li>
	 * </ul>
	 */
	@NotNull
	protected TestPlanStats runTestPlan(String testPlanName, String testName, AddThreadGroupChildren extension)
		throws IOException, InterruptedException, TimeoutException {
		Map<String, AddThreadGroupChildren> extensions = new HashMap<>();
		extensions.put(testName, extension);
		return runTestPlan(testPlanName, extensions);
	}

	/**
	 * create a test plan with one or more thread groups (threadGroupExtensions).
	 * after a test plan is created, this method will proceed to run it.
	 * <p>
	 * supported configurations:<ul>
	 * <li>{TestName}.threads, default to 1</li>
	 * <li>{TestName}.iterations, default to 1</li>
	 * <li>{TestName}.testData for CSV test data file</li>
	 * <li>{TestName}.randomTestData for random CSV test access, default to true</li>
	 * <li>{TestName}.clientId for OKTA authentication/JWT</li>
	 * <li>{TestName}.clientSecret for OKTA authentication/JWT</li>
	 * <li>{TestName}.varName to capture generated JWT</li>
	 * </ul>
	 */
	@NotNull
	protected TestPlanStats runTestPlan(String testPlanName, Map<String, AddThreadGroupChildren> threadGroupExtensions)
		throws IOException, InterruptedException, TimeoutException {

		if (StringUtils.isBlank(testPlanName)) { throw new IllegalArgumentException("testPlanName is required"); }
		if (MapUtils.isEmpty(threadGroupExtensions)) {
			throw new IllegalArgumentException("At least one thread group extension is required");
		}

		// print banner
		String mainTestName = threadGroupExtensions.size() == 1 ?
		                      threadGroupExtensions.keySet().stream().toList().get(0) : testPlanName;
		logBanner(testPlanName, mainTestName);

		TestLibrary lib = new TestLibrary();
		List<TestPlanChild> planItems = newTestPlanItems(mainTestName);

		threadGroupExtensions.forEach((testName, extension) -> {
			List<ThreadGroupChild> threadGroupChildren = new ArrayList<>();

			int initialDelayMs = setting(testName + INITIAL_DELAY_MS, 0);
			if (initialDelayMs > 0) { threadGroupChildren.add(constantTimer(Duration.ofMillis(initialDelayMs))); }

			// add CSV data set if so configured
			String csvData = setting(testName + TEST_DATA);
			boolean randomCsvAccess = setting(testName + RANDOM_TEST_DATA, true);
			if (StringUtils.isNotBlank(csvData)) {
				threadGroupChildren.add(csvDataSet(new TestResource(csvData)).randomOrder(randomCsvAccess));
			}

			// add OKTA auth test element if so configured
			String clientId = setting(testName + CLIENT_ID);
			String clientSecret = setting(testName + CLIENT_SECRET);
			String tokenVarName = StringUtils.defaultIfBlank(setting(testName + VAR_NAME), KEY_AUTH_TOKEN);
			if (StringUtils.isNotBlank(clientId) && StringUtils.isNotBlank(clientSecret)) {
				threadGroupChildren.add(lib.oktaAuth(clientId, clientSecret, tokenVarName));
			}


			// extension point for additional thread group children
			threadGroupChildren.addAll(extension.add(lib));

			long maxDelay = setting(testName + POST_ITER_MAX_DELAY_MS, 0);
			if (maxDelay > 500) {
				threadGroupChildren.add(uniformRandomTimer(Duration.ofMillis(0), Duration.ofMillis(maxDelay)));
			}

			int threads = setting(testName + THREADS, 1);
			int iterations = setting(testName + ITERATIONS, 1);
			double rampUpSec = setting(testName + RAMP_UP_SEC, 0d);
			if (rampUpSec <= 0) {
				logger.info("[{}] adding thread group {} with {} threads, each with {} iterations...",
				            testPlanName, testName, threads, iterations);
				planItems.add(threadGroup(testName, threads, iterations,
				                          threadGroupChildren.toArray(new ThreadGroupChild[0])));
			} else {
				logger.info("[{}] adding thread group {} with {} threads ramp up in {} sec, each with {} iterations...",
				            testPlanName, testName, threads, rampUpSec, iterations);
				planItems.add(threadGroup(testName)
					              .rampTo(threads, Duration.ofMillis((long) rampUpSec * 1000))
					              .holdIterating((iterations))
					              .children(threadGroupChildren.toArray(new ThreadGroupChild[0])));
			}
		});

		// run test plan and return stats for further assertions
		return runTestPlan(mainTestName, testPlan(planItems.toArray(new TestPlanChild[0])));
	}

	protected List<TestPlanChild> newTestPlanItems(String testName) {
		List<TestPlanChild> planItems = new ArrayList<>();
		addCommonPlanItems(testName, planItems);
		return planItems;
	}

	protected void addCommonPlanItems(String testName, List<TestPlanChild> items) {
		items.add(httpCache().disable());
		items.add(httpCookies().clearCookiesBetweenIterations(true).cookiePolicy(STANDARD));
		// always add vars... should be generally useful to have this
		items.add(vars());

		boolean remote = setting(JMETER_RUN_ON_AZURE, false);

		if (setting("jmeter.showViz", false) && !remote) {
			items.add(resultsTreeVisualizer());
			items.add(dashboardVisualizer());
		}

		try { Thread.sleep(250); } catch (InterruptedException e) { /* ignore */ }
		String timestamp = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd_HHmmss.SSSS");
		if (setting(JMETER_HTML_ENABLED, false) && !remote) {
			items.add(htmlReporter(setting(JMETER_HTML_PATH) + "/" + timestamp, testName));
		}

		if (setting(JMETER_JTL_ENABLED, false)) {
			items.add(jtlWriter(setting(JMETER_JTL_PATH) + "/" + timestamp, testName + JTL_EXT));
		}
	}

	@NotNull
	protected AzureEngine newAzureEngine(String testName) {
		AzureEngine azureEngine = new AzureEngine(setting("AZURE_LOAD_TESTING_CREDS"))
			.subscriptionId(setting("AZURE_LOAD_TESTING_SUBSCRIPTION"))
			.resourceGroupName(setting("AZURE_LOAD_TESTING_RESOURCE_GROUP"))
			.testResourceName(setting("AZURE_LOAD_TESTING_RESOURCE"))
			.testName(testName)
			.engines(setting("azure.engine", 1))
			.splitCsvsBetweenEngines(setting("azure.splitCsvsBetweenEngines", false))
			.location(StringUtils.defaultString(setting("azure.location"), "westus"));

		int timeoutSeconds = setting("azure.timeOutSeconds", 0);
		if (timeoutSeconds > 0) { azureEngine.testTimeout(Duration.ofSeconds(timeoutSeconds)); }

		String azureResources = setting("azure.monitoredResources");
		if (StringUtils.isNotBlank(azureResources)) { azureEngine.monitoredResources(azureResources.split(",")); }

		return azureEngine;
	}

	@NotNull
	protected TestPlanStats runTestPlan(String testName, DslTestPlan testPlan)
		throws IOException, InterruptedException, TimeoutException {

		TestPlanStats stats;
		if (setting(JMETER_RUN_ON_AZURE, false)) {
			logger.info("running test plan on Azure...");
			stats = testPlan.runIn(newAzureEngine(testName));
		} else {
			logger.info("running test plan locally...");
			Thread.sleep(RandomUtils.nextInt(3000, 7500));
			stats = testPlan.run();
			if (setting(JMETER_SHOW_GUI, false)) { testPlan.showInGui(); }
		}

		assert stats != null;
		printTestStats(stats);
		return stats;
	}

	protected void printTestStats(TestPlanStats stats) {
		if (logger.isInfoEnabled()) {
			String divider = StringUtils.repeat("-", 80);
			StringBuilder output = new StringBuilder("\n\n");

			stats.labels().forEach(testStats -> {
				StatsSummary summary = stats.byLabel(testStats);
				CountMetricSummary counts = summary.samples();
				TimeMetricSummary sample = summary.sampleTime();
				long duration = summary.endTime().toEpochMilli() - summary.firstTime().toEpochMilli();

				output.append(divider).append("\n");
				output.append("| ").append(StringUtils.center(testStats + " :: Performance Stats", 76)).append(" |")
				      .append("\n");
				output.append(divider).append("\n");
				output.append(formatStatDetail("time", formatEpochTime(summary.firstTime().toEpochMilli()) + " - " +
				                                       formatEpochTime(summary.endTime().toEpochMilli()) + " (" +
				                                       formatStatTime(duration) + ")"))
				      .append("\n");
				output.append(formatStatDetail("total", "" + counts.total()))
				      .append(formatStatDetail("average", formatAvg(counts.perSecond()) + "/s"))
				      .append(formatStatDetail("errors", "" + summary.errorsCount()))
				      .append("\n");
				output.append(formatStatDetail("min", formatStatTime(sample.min())))
				      .append(formatStatDetail("mean", formatStatTime(sample.mean())))
				      .append(formatStatDetail("max", formatStatTime(sample.max()))).append("\n");
				output.append(formatStatDetail("90%", formatStatTime(sample.perc90())))
				      .append(formatStatDetail("95%", formatStatTime(sample.perc95())))
				      .append(formatStatDetail("99%", formatStatTime(sample.perc99()))).append("\n");
				output.append("\n");
			});

			logger.info(output.toString());
		}
	}

	protected static String formatStatTime(Duration statTime) {
		return statTime == null ? "N/A" : MILLIS_FORMAT.format(statTime.toMillis()) + " ms";
	}

	protected static String formatStatTime(long statTime) { return MILLIS_FORMAT.format(statTime) + " ms"; }

	protected static String formatEpochTime(long epoch) { return LONG_DATE_FORMATTER.format(new Date(epoch)); }

	protected static String formatAvg(double avg) { return AVG_FORMATTER.format(avg); }

	protected static String formatStatDetail(String label, String value) {
		return "\t" + StringUtils.leftPad(label, 9) + ": " + StringUtils.leftPad(value, 9);
	}
}
