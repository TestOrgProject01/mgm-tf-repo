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

@Execution(CONCURRENT)
@TestInstance(PER_METHOD)
public class OverrideSearchPerfTest extends BaseTestPlanLibrary {
	private static final String TEST_SUITE = "Override Search Performance Tests";

	@Test
	void overrideSearchViaGuestId() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.overrideSearchViaGuestId()));

		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}

	@Test
	void overrideSearchViaRequestorId() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.overrideSearchViaRequestorId()));

		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}

	@Test
	void overrideSearchViaStatus() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.overrideSearchViaStatus()));

		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}

	@Test
	void overrideSearchViaCategory() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.overrideSearchViaCategory()));

		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}

	@Test
	void overrideSearchViaGuestIdAndStatus() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats =
			runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.overrideSearchViaGuestIdAndStatus()));

		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}

	@Test
	void overrideSearchViaMultipleInputs() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.overrideSearchViaMultipleInputs()));

		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}

	@Test
	void overrideStatusViaGuestId() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.overrideStatusViaGuestId()));

		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}

	@Test
	void overrideDetail() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.overrideDetail()));

		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}
}
