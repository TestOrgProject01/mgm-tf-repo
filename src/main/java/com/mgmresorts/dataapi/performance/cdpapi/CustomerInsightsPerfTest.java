package com.mgmresorts.dataapi.performance.cdpapi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import us.abstracta.jmeter.javadsl.core.stats.StatsSummary;

import java.time.Duration;
import java.util.List;

import static com.mgmresorts.dataapi.performance.cdpapi.TestHelper.currentTestMethodName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD;
import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

@SuppressWarnings({"java:S5960", "java:S112"})
@Execution(CONCURRENT)
@TestInstance(PER_METHOD)
public class CustomerInsightsPerfTest extends BaseTestPlanLibrary {
	private static final String TEST_SUITE = "Customer Insights Performance Tests";

	@Test
	void customerInsightsViaGuestId() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.customerInsightsViaGuestId()));

		// localized/junit-style assertions
		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(10));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}

	@Test
	void customerInsightsViaAccount() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.customerInsightsViaAccount()));

		// localized/junit-style assertions
		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(10));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}

	@Test
	void customerInsightsViaMLifeId() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.customerInsightsViaMLifeId()));

		// localized/junit-style assertions
		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(10));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}
}
