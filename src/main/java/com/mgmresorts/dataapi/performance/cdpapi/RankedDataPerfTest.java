package com.mgmresorts.dataapi.performance.cdpapi;

import org.junit.jupiter.api.Test;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import us.abstracta.jmeter.javadsl.core.stats.StatsSummary;

import java.time.Duration;
import java.util.List;

import static com.mgmresorts.dataapi.performance.cdpapi.TestHelper.currentTestMethodName;
import static org.assertj.core.api.Assertions.assertThat;

public class RankedDataPerfTest extends BaseTestPlanLibrary {
	private static final String TEST_SUITE = "Ranked Data Performance Tests";

	@Test
	void rankedData() throws Exception {
		String testName = currentTestMethodName();
		TestPlanStats stats = runTestPlan(TEST_SUITE, testName, lib -> List.of(lib.rankedData()));

		StatsSummary testStats = stats.byLabel(testName);
		assertThat(testStats.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(2));
		assertThat(testStats.sampleTime().max()).isLessThan(Duration.ofSeconds(3));
		assertThat(testStats.sampleTime().mean()).isLessThan(Duration.ofSeconds(1));
		assertThat(testStats.errorsCount()).isEqualTo(0);
	}
}