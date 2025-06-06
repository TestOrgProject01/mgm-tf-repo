package com.mgmresorts.dataapi.performance.cdpapi;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import us.abstracta.jmeter.javadsl.azure.AzureEngine;
import us.abstracta.jmeter.javadsl.core.DslTestPlan;
import us.abstracta.jmeter.javadsl.core.DslTestPlan.TestPlanChild;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import us.abstracta.jmeter.javadsl.core.configs.DslVariables;
import us.abstracta.jmeter.javadsl.core.controllers.DslOnceOnlyController;
import us.abstracta.jmeter.javadsl.core.stats.TimeMetricSummary;
import us.abstracta.jmeter.javadsl.http.DslHttpSampler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.mgmresorts.dataapi.performance.cdpapi.Constants.HEADER_X_FUNCTION_KEY;
import static com.mgmresorts.dataapi.performance.cdpapi.Constants.KEY_AUTH_TOKEN;
import static com.mgmresorts.dataapi.performance.cdpapi.TestHelper.collectInput;
import static com.mgmresorts.dataapi.performance.cdpapi.TestHelper.printTestTitle;
import static com.mgmresorts.dataapi.performance.dsl.ExtendedJmeterDsl.httpResponseCodeAssertion;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
import static us.abstracta.jmeter.javadsl.dashboard.DashboardVisualizer.dashboardVisualizer;
import static us.abstracta.jmeter.javadsl.http.DslCookieManager.CookiePolicy.STANDARD;

class FirstTest extends BasePerfLibrary {
	private int numberOfThreads = 10;
	private int numberOfIterations = 5;
	private boolean runInAzure = false;

	private boolean testHealthChecks = true;
	private boolean testBff = false;
	private boolean collectHtmlReport = true;

	@Test
	void firstTest() throws Exception {
		printTestTitle();

		logger.info("create test plan with {} threads, each with {} iterations...",
		            numberOfThreads, numberOfIterations);
		DslTestPlan testPlan = testPlan(collectTestPlanItems());

		TestPlanStats stats;
		if (runInAzure) {
			logger.info("running test plan on Azure...");
			stats = testPlan.runIn(
				new AzureEngine(setting("AZURE_LOAD_TESTING_CREDS"))
					.subscriptionId(setting("AZURE_LOAD_TESTING_SUBSCRIPTION"))
					.resourceGroupName(setting("AZURE_LOAD_TESTING_RESOURCE_GROUP"))
					.testResourceName(setting("AZURE_LOAD_TESTING_RESOURCE"))
					.testName("dsl-test")
					.engines(2)
					.testTimeout(Duration.ofMinutes(10))
			);
		} else {
			logger.info("running test plan locally...");
			stats = testPlan.run();
			testPlan.showInGui();
		}

		assertNotNull(stats);
		TimeMetricSummary overallSampleTime = stats.overall().sampleTime();
		logger.info(
			"""
			test stats:
			min: {}\tmax: {}\tmean: {}
			90%: {}\t95%: {}\t99%: {}
			""",
			StringUtils.rightPad(String.valueOf(overallSampleTime.min().get(SECONDS)), 5),
			StringUtils.rightPad(String.valueOf(overallSampleTime.max().get(SECONDS)), 5),
			StringUtils.rightPad(String.valueOf(overallSampleTime.mean().get(SECONDS)), 5),
			StringUtils.rightPad(String.valueOf(overallSampleTime.perc90().get(SECONDS)), 5),
			StringUtils.rightPad(String.valueOf(overallSampleTime.perc95().get(SECONDS)), 5),
			StringUtils.rightPad(String.valueOf(overallSampleTime.perc99().get(SECONDS)), 5)
		);

		// localized/junit-style assertions
		assertThat(stats.overall().sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(stats.overall().sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(stats.overall().sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
	}

	@NotNull
	private TestPlanChild[] collectTestPlanItems() {
		// 1. create CSV
		// 2. encrypt CSV - check in
		// 3. at runtime, use std in to request for encryption key
		// 4. use encryption key to decrypt CSV, create new file (.csv) and copy to project directory
		// 5. run test in azure
		// String csvFileEncrypt = "src/test/resources/data/employee-ids.{ENV}.zip";
		// String csvFile = "src/test/resources/data/employee-ids.{ENV}.csv";


		DslVariables vars = vars();

		List<TestPlanChild> testPlanChildren = new ArrayList<>();
		// testPlanChildren.add(csvDataSet(csvFile));
		testPlanChildren.add(vars);
		testPlanChildren.add(httpCache().disable());
		testPlanChildren.add(httpCookies().clearCookiesBetweenIterations(true).cookiePolicy(STANDARD));

		if (testHealthChecks) {
			testPlanChildren.add(threadGroup("heath-checks", numberOfThreads, numberOfIterations, healthChecks()));
		}

		if (testBff) {
			String jwt = collectInput("Enter a valid EMPLOYEE JWT:");
			vars.set(KEY_AUTH_TOKEN, jwt);
			testPlanChildren.add(threadGroup("bff-references", numberOfThreads, numberOfIterations, bffReferences()));
		}

		// oktaAuth("customer_data_product_services", setting("CUSTOMER_DATA_PRODUCT_SERVICES_SECRET")),

		testPlanChildren.add(resultsTreeVisualizer());
		testPlanChildren.add(dashboardVisualizer());

		if (collectHtmlReport) { testPlanChildren.add(htmlReporter("target/jtls")); }
		testPlanChildren.add(jtlWriter("target/jtls"));

		return testPlanChildren.toArray(new TestPlanChild[0]);
	}

	@NotNull
	private DslOnceOnlyController oktaAuth(String clientId, String clientSecret, String varName) {
		return onceOnlyController(
			httpSampler("auth", setting("IDENTITY_URL"))
				.method("POST")
				.header("Content-Type", "application/x-www-form-urlencoded")
				.body("grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret)
				.children(
					httpResponseCodeAssertion(200),
					jsonExtractor(varName, "access_token")));
	}

	private DslHttpSampler healthChecks() {
		return httpSampler("heath-check", setting("AZURE_HOST") + "/healthcheck")
			.method("GET")
			.param("cs", "T")
			.param("cd", "T")
			.header("Content-Type", "application/json")
			.children(
				httpResponseCodeAssertion(200)
			);
	}

	private DslHttpSampler bffReferences() {
		return httpSampler("bff-references", setting("AZURE_HOST") + "/customer-search/references")
			.method("GET")
			.header("Authorization", "Bearer ${" + KEY_AUTH_TOKEN + "}")
			.header(HEADER_X_FUNCTION_KEY, setting("BFF_API_KEY"))
			.header("Content-Type", "application/json");
	}
}
