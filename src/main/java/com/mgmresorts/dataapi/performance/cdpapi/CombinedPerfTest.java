package com.mgmresorts.dataapi.performance.cdpapi;

import org.junit.jupiter.api.Test;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import us.abstracta.jmeter.javadsl.core.stats.StatsSummary;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"java:S5960", "java:S112", "java:S1181", "java:S100", "java:S106"})
public class CombinedPerfTest extends BaseTestPlanLibrary {
	private static final String TEST_CUSTOMER_SEARCH_VIA_GUEST_ID = "customerSearchViaGuestId";
	private static final String TEST_CUSTOMER_SEARCH_VIA_MLIFE_ID = "customerSearchViaMLifeId";
	private static final String TEST_CUSTOMER_SEARCH_VIA_AMPERITY_ID = "customerSearchViaAmperityId";
	private static final String TEST_CUSTOMER_SEARCH_VIA_ACCOUNT = "customerSearchViaAccount";
	private static final String TEST_NAME_UNSURVIVED_PHONE_1 = "customerSearchViaUnsurvivedPhoneMultiResults";
	private static final String TEST_NAME_UNSURVIVED_PHONE_2 = "customerSearchViaUnsurvivedPhoneSingleResults";
	private static final String TEST_NAME_UNSURVIVED_PHONE_3 = "customerSearchViaUnsurvivedPhoneZeroResults";
	private static final String TEST_CUSTOMER_INSIGHTS_VIA_GUEST_ID = "customerInsightsViaGuestId";
	private static final String TEST_CUSTOMER_INSIGHTS_VIA_MLIFE_ID = "customerInsightsViaMLifeId";
	private static final String TEST_CUSTOMER_INSIGHTS_VIA_ACCOUNT = "customerInsightsViaAccount";
	private static final String OVERRIDE_SEARCH_VIA_GUEST_ID = "overrideSearchViaGuestId";
	private static final String OVERRIDE_SEARCH_VIA_CATEGORY = "overrideSearchViaCategory";
	private static final String OVERRIDE_SEARCH_VIA_REQUESTOR_ID = "overrideSearchViaRequestorId";
	private static final String OVERRIDE_SEARCH_VIA_STATUS = "overrideSearchViaStatus";
	private static final String OVERRIDE_SEARCH_VIA_GUEST_ID_AND_STATUS = "overrideSearchViaGuestIdAndStatus";
	private static final String OVERRIDE_SEARCH_VIA_MULTIPLE_INPUTS = "overrideSearchViaMultipleInputs";
	private static final String OVERRIDE_STATUS_VIA_GUEST_ID = "overrideStatusViaGuestId";
	private static final String RANKED_DATA = "rankedData";

	@Test
	void customer_search_and_insights() throws Exception {
		String testSuite = "Customer Search and Insights Performance Tests";

		Map<String, AddThreadGroupChildren> testConfig = new HashMap<>();
		addCustomerSearchTests(testConfig);
		addCustomerInsightsTests(testConfig);
		TestPlanStats stats = runTestPlan(testSuite, testConfig);

		// localized/junit-style assertions
		assertResultsForCustomerSearch(stats);
		assertResultsForCustomerInsights(stats);
	}

	@Test
	void all_cdp_apis() throws Exception {
		String testSuite = "CDP API Performance Tests";

		Map<String, AddThreadGroupChildren> testConfig = new HashMap<>();
		addCustomerSearchTests(testConfig);
		addCustomerInsightsTests(testConfig);
		addOverrideTests(testConfig);
		TestPlanStats stats = runTestPlan(testSuite, testConfig);

		// localized/junit-style assertions
		assertResultsForCustomerSearch(stats);
		assertResultsForCustomerInsights(stats);
		assertResultsForOverride(stats);
	}

	@Test
	void customer_search() throws Exception {
		String testSuite = "Customer Search Performance Tests";

		Map<String, AddThreadGroupChildren> testConfig = new HashMap<>();
		addCustomerSearchTests(testConfig);
		TestPlanStats stats = runTestPlan(testSuite, testConfig);

		// localized/junit-style assertions
		assertResultsForCustomerSearch(stats);
	}

	@Test
	void customer_insights() throws Exception {
		String testSuite = "Customer Insights Performance Tests";

		Map<String, AddThreadGroupChildren> testConfig = new HashMap<>();
		addCustomerInsightsTests(testConfig);
		TestPlanStats stats = runTestPlan(testSuite, testConfig);

		// localized/junit-style assertions
		assertResultsForCustomerInsights(stats);
	}

	@Test
	void override() throws Exception {
		String testSuite = "Override API Performance Tests";

		Map<String, AddThreadGroupChildren> testConfig = new HashMap<>();
		addOverrideTests(testConfig);
		TestPlanStats stats = runTestPlan(testSuite, testConfig);

		// localized/junit-style assertions
		assertResultsForOverride(stats);
	}

	@Test
	void rankedData() throws Exception {
		String testSuite = "Ranked Data Performance Tests";

		Map<String, AddThreadGroupChildren> testConfig = new HashMap<>();
		testConfig.put(RANKED_DATA, lib -> List.of(lib.rankedData()));
		TestPlanStats planStats = runTestPlan(testSuite, testConfig);

		// localized/junit-style assertions
		try {
			StatsSummary stats = planStats.byLabel(RANKED_DATA);
			assertThat(stats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(8));
			assertThat(stats.sampleTime().max()).isLessThan(Duration.ofSeconds(10));
			assertThat(stats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(stats.errorsCount()).isEqualTo(0);
		} catch (Exception e) {
			printFailure(RANKED_DATA, e);
		}

	}

	private static void addCustomerSearchTests(Map<String, AddThreadGroupChildren> testConfig) {
		testConfig.put(TEST_CUSTOMER_SEARCH_VIA_GUEST_ID, lib -> List.of(lib.customerSearchViaGuestId()));
		testConfig.put(TEST_CUSTOMER_SEARCH_VIA_MLIFE_ID, lib -> List.of(lib.customerSearchViaMLifeId()));
		testConfig.put(TEST_CUSTOMER_SEARCH_VIA_AMPERITY_ID, lib -> List.of(lib.customerSearchViaAmperityId()));
		testConfig.put(TEST_CUSTOMER_SEARCH_VIA_ACCOUNT, lib -> List.of(lib.customerSearchViaAccount()));
		testConfig.put(TEST_NAME_UNSURVIVED_PHONE_1,
		               lib -> List.of(lib.customerSearchViaUnsurvivedPhone(TEST_NAME_UNSURVIVED_PHONE_1)));
		testConfig.put(TEST_NAME_UNSURVIVED_PHONE_2,
		               lib -> List.of(lib.customerSearchViaUnsurvivedPhone(TEST_NAME_UNSURVIVED_PHONE_2)));
		testConfig.put(TEST_NAME_UNSURVIVED_PHONE_3,
		               lib -> List.of(lib.customerSearchViaUnsurvivedPhone(TEST_NAME_UNSURVIVED_PHONE_3)));
	}

	private static void addCustomerInsightsTests(Map<String, AddThreadGroupChildren> testConfig) {
		testConfig.put(TEST_CUSTOMER_INSIGHTS_VIA_GUEST_ID, lib -> List.of(lib.customerInsightsViaGuestId()));
		testConfig.put(TEST_CUSTOMER_INSIGHTS_VIA_MLIFE_ID, lib -> List.of(lib.customerInsightsViaMLifeId()));
		testConfig.put(TEST_CUSTOMER_INSIGHTS_VIA_ACCOUNT, lib -> List.of(lib.customerInsightsViaAccount()));
	}

	private static void addOverrideTests(Map<String, AddThreadGroupChildren> testConfig) {
		testConfig.put(OVERRIDE_SEARCH_VIA_GUEST_ID, lib -> List.of(lib.overrideSearchViaGuestId()));
		testConfig.put(OVERRIDE_SEARCH_VIA_CATEGORY, lib -> List.of(lib.overrideSearchViaCategory()));
		testConfig.put(OVERRIDE_SEARCH_VIA_REQUESTOR_ID, lib -> List.of(lib.overrideSearchViaRequestorId()));
		testConfig.put(OVERRIDE_SEARCH_VIA_STATUS, lib -> List.of(lib.overrideSearchViaStatus()));
		testConfig.put(OVERRIDE_SEARCH_VIA_GUEST_ID_AND_STATUS,
		               lib -> List.of(lib.overrideSearchViaGuestIdAndStatus()));
		testConfig.put(OVERRIDE_SEARCH_VIA_MULTIPLE_INPUTS, lib -> List.of(lib.overrideSearchViaMultipleInputs()));
		testConfig.put(OVERRIDE_STATUS_VIA_GUEST_ID, lib -> List.of(lib.overrideStatusViaGuestId()));
	}

	private static void assertResultsForCustomerSearch(TestPlanStats stats) {
		try {
			StatsSummary testStats = stats.byLabel(TEST_CUSTOMER_SEARCH_VIA_GUEST_ID);
			assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(5));
			assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(8));
			assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(testStats.errorsCount()).isEqualTo(0);
		} catch (Throwable e) {
			printFailure(TEST_CUSTOMER_SEARCH_VIA_GUEST_ID, e);
		}

		try {
			StatsSummary testStats = stats.byLabel(TEST_CUSTOMER_SEARCH_VIA_MLIFE_ID);
			assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(5));
			assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(8));
			assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(testStats.errorsCount()).isEqualTo(0);
		} catch (Throwable e) {
			printFailure(TEST_CUSTOMER_SEARCH_VIA_MLIFE_ID, e);
		}

		try {
			StatsSummary testStats = stats.byLabel(TEST_CUSTOMER_SEARCH_VIA_AMPERITY_ID);
			assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(5));
			assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(8));
			assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(testStats.errorsCount()).isEqualTo(0);
		} catch (Throwable e) {
			printFailure(TEST_CUSTOMER_SEARCH_VIA_AMPERITY_ID, e);
		}

		try {
			StatsSummary testStats = stats.byLabel(TEST_CUSTOMER_SEARCH_VIA_ACCOUNT);
			assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(5));
			assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(8));
			assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(testStats.errorsCount()).isEqualTo(0);
		} catch (Throwable e) {
			printFailure(TEST_CUSTOMER_SEARCH_VIA_ACCOUNT, e);
		}

		try {
			StatsSummary testStats = stats.byLabel(TEST_NAME_UNSURVIVED_PHONE_1);
			assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(8));
			assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
			assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(testStats.errorsCount()).isEqualTo(0);
		} catch (Throwable e) {
			printFailure(TEST_NAME_UNSURVIVED_PHONE_1, e);
		}

		try {
			StatsSummary testStats = stats.byLabel(TEST_NAME_UNSURVIVED_PHONE_2);
			assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(8));
			assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
			assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(testStats.errorsCount()).isEqualTo(0);
		} catch (Throwable e) {
			printFailure(TEST_NAME_UNSURVIVED_PHONE_2, e);
		}

		try {
			StatsSummary testStats = stats.byLabel(TEST_NAME_UNSURVIVED_PHONE_3);
			assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(8));
			assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
			assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(testStats.errorsCount()).isEqualTo(0);
		} catch (Throwable e) {
			printFailure(TEST_NAME_UNSURVIVED_PHONE_3, e);
		}
	}

	private static void assertResultsForCustomerInsights(TestPlanStats stats) {
		try {
			StatsSummary customerInsightsViaGuestIdStats = stats.byLabel(TEST_CUSTOMER_INSIGHTS_VIA_GUEST_ID);
			assertThat(customerInsightsViaGuestIdStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(8));
			assertThat(customerInsightsViaGuestIdStats.sampleTime().max()).isLessThan(Duration.ofSeconds(10));
			assertThat(customerInsightsViaGuestIdStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(customerInsightsViaGuestIdStats.errorsCount()).isEqualTo(0);
		} catch (Exception e) {
			printFailure(TEST_CUSTOMER_INSIGHTS_VIA_GUEST_ID, e);
		}

		try {
			StatsSummary customerInsightsViaGuestIdStats = stats.byLabel(TEST_CUSTOMER_INSIGHTS_VIA_MLIFE_ID);
			assertThat(customerInsightsViaGuestIdStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(8));
			assertThat(customerInsightsViaGuestIdStats.sampleTime().max()).isLessThan(Duration.ofSeconds(10));
			assertThat(customerInsightsViaGuestIdStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(customerInsightsViaGuestIdStats.errorsCount()).isEqualTo(0);
		} catch (Exception e) {
			printFailure(TEST_CUSTOMER_INSIGHTS_VIA_MLIFE_ID, e);
		}

		try {
			StatsSummary customerInsightsViaGuestIdStats = stats.byLabel(TEST_CUSTOMER_INSIGHTS_VIA_ACCOUNT);
			assertThat(customerInsightsViaGuestIdStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(8));
			assertThat(customerInsightsViaGuestIdStats.sampleTime().max()).isLessThan(Duration.ofSeconds(10));
			assertThat(customerInsightsViaGuestIdStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(customerInsightsViaGuestIdStats.errorsCount()).isEqualTo(0);
		} catch (Exception e) {
			printFailure(TEST_CUSTOMER_INSIGHTS_VIA_ACCOUNT, e);
		}
	}

	private static void assertResultsForOverride(TestPlanStats planStats) {
		try {
			StatsSummary stats = planStats.byLabel(OVERRIDE_SEARCH_VIA_GUEST_ID);
			assertThat(stats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(8));
			assertThat(stats.sampleTime().max()).isLessThan(Duration.ofSeconds(10));
			assertThat(stats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(stats.errorsCount()).isEqualTo(0);
		} catch (Exception e) {
			printFailure(OVERRIDE_SEARCH_VIA_GUEST_ID, e);
		}

		try {
			StatsSummary stats = planStats.byLabel(OVERRIDE_SEARCH_VIA_CATEGORY);
			assertThat(stats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(8));
			assertThat(stats.sampleTime().max()).isLessThan(Duration.ofSeconds(10));
			assertThat(stats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(stats.errorsCount()).isEqualTo(0);
		} catch (Exception e) {
			printFailure(OVERRIDE_SEARCH_VIA_CATEGORY, e);
		}

		try {
			StatsSummary stats = planStats.byLabel(OVERRIDE_SEARCH_VIA_REQUESTOR_ID);
			assertThat(stats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(8));
			assertThat(stats.sampleTime().max()).isLessThan(Duration.ofSeconds(10));
			assertThat(stats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(stats.errorsCount()).isEqualTo(0);
		} catch (Exception e) {
			printFailure(OVERRIDE_SEARCH_VIA_REQUESTOR_ID, e);
		}

		try {
			StatsSummary stats = planStats.byLabel(OVERRIDE_SEARCH_VIA_STATUS);
			assertThat(stats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(8));
			assertThat(stats.sampleTime().max()).isLessThan(Duration.ofSeconds(10));
			assertThat(stats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(stats.errorsCount()).isEqualTo(0);
		} catch (Exception e) {
			printFailure(OVERRIDE_SEARCH_VIA_STATUS, e);
		}

		try {
			StatsSummary stats = planStats.byLabel(OVERRIDE_SEARCH_VIA_GUEST_ID_AND_STATUS);
			assertThat(stats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(8));
			assertThat(stats.sampleTime().max()).isLessThan(Duration.ofSeconds(10));
			assertThat(stats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(stats.errorsCount()).isEqualTo(0);
		} catch (Exception e) {
			printFailure(OVERRIDE_SEARCH_VIA_GUEST_ID_AND_STATUS, e);
		}

		try {
			StatsSummary stats = planStats.byLabel(OVERRIDE_SEARCH_VIA_MULTIPLE_INPUTS);
			assertThat(stats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(8));
			assertThat(stats.sampleTime().max()).isLessThan(Duration.ofSeconds(10));
			assertThat(stats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(stats.errorsCount()).isEqualTo(0);
		} catch (Exception e) {
			printFailure(OVERRIDE_SEARCH_VIA_MULTIPLE_INPUTS, e);
		}

		try {
			StatsSummary stats = planStats.byLabel(OVERRIDE_STATUS_VIA_GUEST_ID);
			assertThat(stats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(8));
			assertThat(stats.sampleTime().max()).isLessThan(Duration.ofSeconds(10));
			assertThat(stats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
			assertThat(stats.errorsCount()).isEqualTo(0);
		} catch (Exception e) {
			printFailure(OVERRIDE_STATUS_VIA_GUEST_ID, e);
		}

	}

	private static void printFailure(String test, Throwable e) {
		System.err.println("[" + test + "]: FAIL " + e.getMessage());
	}
}
