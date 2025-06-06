package com.mgmresorts.dataapi.performance.dsl;

import org.junit.jupiter.api.Test;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;

import static com.mgmresorts.dataapi.performance.dsl.ExtendedJmeterDsl.jsonExtendedAssertion;
import static org.assertj.core.api.Assertions.assertThat;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

class JsonExtendedAssertionTest {

	@Test
	void null_test() throws Exception {
		TestPlanStats stats =
			testPlan(
				vars().set("EXPECTED", "null"),
				threadGroup(1, 1,
				            dummySampler("{\"prop\": null}"),
				            dummySampler("{}"),
				            jsonExtendedAssertion("prop Assertion", "prop", "${EXPECTED}")
				)
			).run();
		assertThat(stats.overall().errorsCount()).isZero();
	}

	@Test
	void numeric_comparison() throws Exception {
		{
			TestPlanStats stats =
				testPlan(
					vars().set("EXPECTED", "0001.00"),
					threadGroup(1, 1,
					            dummySampler("{\"prop\": 1.0000}"),
					            jsonExtendedAssertion("prop Assertion", "$.prop", "${EXPECTED}").compareAsNumber()
					)
				).run();
			assertThat(stats.overall().errorsCount()).isZero();
		}
		{
			TestPlanStats stats =
				testPlan(
					vars().set("EXPECTED", "1"),
					threadGroup(1, 1,
					            dummySampler("{\"prop\": 1.0000}"),
					            jsonExtendedAssertion("prop Assertion", "$.prop", "${EXPECTED}").compareAsNumber()
					)
				).run();
			assertThat(stats.overall().errorsCount()).isZero();
		}
		{
			TestPlanStats stats =
				testPlan(
					vars().set("EXPECTED", "1.0"),
					threadGroup(1, 1,
					            dummySampler("{\"prop\": 1}"),
					            jsonExtendedAssertion("prop Assertion", "$.prop", "${EXPECTED}").compareAsNumber()
					)
				).run();
			assertThat(stats.overall().errorsCount()).isZero();
		}
		{
			TestPlanStats stats =
				testPlan(
					vars().set("EXPECTED", "1.0"),
					threadGroup(1, 1,
					            dummySampler("{\"prop\": \"1\"}"),
					            jsonExtendedAssertion("prop Assertion", "$.prop", "${EXPECTED}").compareAsNumber()
					)
				).run();
			assertThat(stats.overall().errorsCount()).isZero();
		}
		{
			TestPlanStats stats =
				testPlan(
					vars().set("EXPECTED", "1.0"),
					threadGroup(1, 1,
					            dummySampler("{\"prop\": \"1.000\"}"),
					            jsonExtendedAssertion("prop Assertion", "$.prop", "${EXPECTED}").compareAsNumber()
					)
				).run();
			assertThat(stats.overall().errorsCount()).isZero();
		}
	}

	@Test
	void numeric_comparison_as_int() throws Exception {
		{
			TestPlanStats stats =
				testPlan(
					vars().set("EXPECTED", "0001.0032"),
					threadGroup(1, 1,
					            dummySampler("{\"prop\": 1.0032}"),
					            jsonExtendedAssertion("prop Assertion", "$.prop", "${EXPECTED}").compareAsInt()
					)
				).run();
			assertThat(stats.overall().errorsCount()).isZero();
		}
		{
			TestPlanStats stats =
				testPlan(
					vars().set("EXPECTED", "1.0010000"),
					threadGroup(1, 1,
					            dummySampler("{\"prop\": 1.0010}"),
					            jsonExtendedAssertion("prop Assertion", "$.prop", "${EXPECTED}").compareAsInt()
					)
				).run();
			assertThat(stats.overall().errorsCount()).isZero();
		}
		{
			TestPlanStats stats =
				testPlan(
					vars().set("EXPECTED", "1.0"),
					threadGroup(1, 1,
					            dummySampler("{\"prop\": 1.0000}"),
					            jsonExtendedAssertion("prop Assertion", "$.prop", "${EXPECTED}").compareAsInt()
					)
				).run();
			assertThat(stats.overall().errorsCount()).isZero();
		}
		{
			TestPlanStats stats =
				testPlan(
					vars().set("EXPECTED", "1.0"),
					threadGroup(1, 1,
					            dummySampler("{\"prop\": \"1.0001\"}"),
					            jsonExtendedAssertion("prop Assertion", "$.prop", "${EXPECTED}").compareAsInt()
					)
				).run();
			assertThat(stats.overall().errorsCount()).isZero();
		}
	}

	@Test
	void array_length_test() throws Exception {
		TestPlanStats stats =
			testPlan(
				vars().set("EXPECTED", "4"),
				threadGroup(1, 1,
				            dummySampler("{" +
				                         "  \"customerInfo\": {" +
				                         "    \"accountInfo\": {" +
				                         "      \"linkedAccounts\": [" +
				                         "        {" +
				                         "          \"accountId\": \"12373361\"" +
				                         "        }," +
				                         "        {" +
				                         "          \"accountId\": \"11845BA6-E5F8-4933-84E9-E51A6E95034C\"" +
				                         "        }," +
				                         "        {" +
				                         "          \"accountId\": \"7751fdbb-2703-3dd5-b855-a5a584333924\"" +
				                         "        }," +
				                         "        {" +
				                         "          \"accountId\": \"9337116\"" +
				                         "        }" +
				                         "      ]" +
				                         "    }" +
				                         "  }" +
				                         "}"),
				            jsonExtendedAssertion("prop Assertion",
				                                  "$.customerInfo.accountInfo.linkedAccounts[*].accountId",
				                                  "${EXPECTED}").compareAsArrayLength()
				)
			).run();
		assertThat(stats.overall().errorsCount()).isZero();
	}
}