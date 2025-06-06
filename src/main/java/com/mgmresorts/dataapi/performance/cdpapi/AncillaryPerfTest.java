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
class AncillaryPerfTest extends BaseTestPlanLibrary {
	private static final String TEST_SUITE = "Ancillary Performance Tests";

	@Test
	void healthChecks() throws Exception {
		TestPlanStats stats = runTestPlan(TEST_SUITE, currentTestMethodName(), lib -> List.of(lib.healthChecks()));

		// localized/junit-style assertions
		StatsSummary statsOverall = stats.overall();
		assertThat(statsOverall.sampleTimePercentile99()).isLessThan(Duration.ofSeconds(3));
		assertThat(statsOverall.sampleTime().max()).isLessThan(Duration.ofSeconds(5));
		assertThat(statsOverall.sampleTime().mean()).isLessThan(Duration.ofSeconds(2));
		assertThat(statsOverall.errorsCount()).isLessThan(1);
	}
}
