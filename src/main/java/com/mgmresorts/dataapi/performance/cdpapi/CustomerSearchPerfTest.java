package com.mgmresorts.dataapi.performance.cdpapi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
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
public class CustomerSearchPerfTest extends BaseTestPlanLibrary {
	private static final String TEST_SUITE = "Customer Search Performance Tests";

	@Test
	@EnabledIfEnvironmentVariable(named = ENV_TESTS, matches = ".*customerSearchViaGuestId.*")
	void customerSearchViaGuestId() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.customerSearchViaGuestId()));

		// localized/junit-style assertions
		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}

	@Test
	@EnabledIfEnvironmentVariable(named = ENV_TESTS, matches = ".*customerSearchViaMLifeId.*")
	void customerSearchViaMLifeId() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.customerSearchViaMLifeId()));

		// localized/junit-style assertions
		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}

	@Test
	@EnabledIfEnvironmentVariable(named = ENV_TESTS, matches = ".*customerSearchViaAmperityId.*")
	void customerSearchViaAmperityId() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.customerSearchViaAmperityId()));

		// localized/junit-style assertions
		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}

	@Test
	void customerSearchViaUnsurvivedPhoneMultiResults() throws Exception {
		customerSearchViaUnsurvivedPhone("customerSearchViaUnsurvivedPhoneMultiResults");
	}

	@Test
	void customerSearchViaUnsurvivedPhoneSingleResults() throws Exception {
		customerSearchViaUnsurvivedPhone("customerSearchViaUnsurvivedPhoneSingleResults");
	}

	@Test
	void customerSearchViaUnsurvivedPhoneZeroResults() throws Exception {
		customerSearchViaUnsurvivedPhone("customerSearchViaUnsurvivedPhoneZeroResults");
	}

	void customerSearchViaUnsurvivedPhone(String testScenario) throws Exception {
		TestPlanStats stats = runTestPlan(TEST_SUITE,
		                                  testScenario,
		                                  lib -> List.of(lib.customerSearchViaUnsurvivedPhone(testScenario)));

		// localized/junit-style assertions
		StatsSummary testStats = stats.byLabel(testScenario);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}

	@Test
	@EnabledIfEnvironmentVariable(named = ENV_TESTS, matches = ".*customerSearchViaAmperityId.*")
	void customerSearchViaAccount() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.customerSearchViaAccount()));

		// localized/junit-style assertions
		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}
}
