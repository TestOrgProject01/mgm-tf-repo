package com.mgmresorts.dataapi.performance.cdpapi;

import org.jetbrains.annotations.NotNull;
import us.abstracta.jmeter.javadsl.core.controllers.DslOnceOnlyController;
import us.abstracta.jmeter.javadsl.core.samplers.BaseSampler.SamplerChild;
import us.abstracta.jmeter.javadsl.http.DslHttpSampler;

import static com.mgmresorts.dataapi.performance.cdpapi.Constants.*;
import static com.mgmresorts.dataapi.performance.cdpapi.TestHelper.currentTestMethodName;
import static com.mgmresorts.dataapi.performance.dsl.ExtendedJmeterDsl.httpResponseCodeAssertion;
import static com.mgmresorts.dataapi.performance.dsl.ExtendedJmeterDsl.jsonExtendedAssertion;
import static org.apache.jmeter.protocol.http.util.HTTPConstants.*;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

public class TestLibrary extends BasePerfLibrary {
	private static final String MSG_ASSERT_MLIFE_ID = "assert mLife ID";
	private static final String MSG_ASSERT_GUEST_ID = "assert guest ID";
	private static final String MSG_ASSERT_REWARDS_TIER = "assert rewards tier";
	private static final String MSG_ASSERT_STATE_CODE = "assert state code";
	private static final String MSG_ASSERT_AMPERITY_ID = "assert amperity ID";
	private static final String KEY_USE_APIGEE = "test.useApigee";
	private static final String DEF_AUTH_VAR = "Bearer ${" + KEY_AUTH_TOKEN + "}";
	private static final String MSG_ASSERT_CUSTOMER_ID = "assert customer ID";
	private static final String MSG_ASSERT_REQUESTOR_ID = "assert requestor ID";
	private static final String MSG_ASSERT_OVERRIDE_STATUS = "assert override status";
	private static final String MSG_ASSERT_OVERRIDE_CATEGORY = "assert override category";

	private static final String JP_GUEST_ID = "$.guestId";

	private final String healthCheckUrl = resolveApiUrl("/healthcheck");
	private final String bffReferencesUrl = resolveApiUrl("/customer-search/references");
	private final String customerSearchUrl = resolveApiUrl("/customerSearch");
	private final String customerInsightsUrl = resolveApiUrl("/retrieveCustomerInsightInfo");
	private final String overrideRequestUrl = resolveApiUrl("/override/requests");
	private final String overrideDetailUrl = resolveApiUrl("/override/request");
	private final String overrideStatusUrl = resolveApiUrl("/override/status");
	private final String rankedDataUrl = resolveApiUrl("/rankedData/list");

	@NotNull
	DslOnceOnlyController oktaAuth(String clientId, String clientSecret, String varName) {
		return onceOnlyController(
			httpSampler(currentTestMethodName(), setting("IDENTITY_URL"))
				.method(POST)
				.header(HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded")
				.body("grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret)
				.children(
					httpResponseCodeAssertion(200),
					jsonExtractor(varName, "access_token")));
	}

	@NotNull
	DslHttpSampler healthChecks() {
		return httpSampler(currentTestMethodName(), healthCheckUrl)
			.method(GET)
			.param("cs", "T")
			.param("cd", "T")
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.children(
				httpResponseCodeAssertion(200),
				jsonExtendedAssertion("assert all passed", "$.all-passed", "true")
			);
	}

	@NotNull
	DslHttpSampler bffReferences() {
		return httpSampler(currentTestMethodName(), bffReferencesUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_X_FUNCTION_KEY, setting("BFF_API_KEY"))
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.children(httpResponseCodeAssertion(200));
	}

	@NotNull
	DslHttpSampler customerSearchViaGuestId() {
		return httpSampler(currentTestMethodName(), customerSearchUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(GUEST_ID, "${" + GUEST_ID + "}")
			.children(customerSearchResultAssertions());
	}

	@NotNull
	DslHttpSampler customerSearchViaMLifeId() {
		return httpSampler(currentTestMethodName(), customerSearchUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(MLIFE_ID, "${" + MLIFE_ID + "}")
			.children(customerSearchResultAssertions());
	}

	@NotNull
	DslHttpSampler customerInsightsViaGuestId() {
		return httpSampler(currentTestMethodName(), customerInsightsUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(GUEST_ID, "${" + GUEST_ID + "}")
			.children(customerInsightsResultAssertions());
	}

	@NotNull
	DslHttpSampler customerInsightsViaMLifeId() {
		return httpSampler(currentTestMethodName(), customerInsightsUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(MLIFE_ID, "${" + MLIFE_ID + "}")
			.children(customerInsightsResultAssertions());
	}

	@NotNull
	DslHttpSampler customerInsightsViaAccount() {
		return httpSampler(currentTestMethodName(), customerInsightsUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(ACCOUNT_TYPE, "${" + ACCOUNT_TYPE + "}")
			.param(ACCOUNT_ID, "${" + ACCOUNT_ID + "}")
			.children(customerInsightsByAccountAssertions());
	}

	@NotNull
	DslHttpSampler customerSearchViaUnsurvivedPhone(String testName) {
		return httpSampler(testName, customerSearchUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(ONLY_UNSURVIVED, "true")
			.param(PHONE_NUMBER, "${" + PHONE_NUMBER + "}")
			.children(customerSearchViaUnSurvivedPhoneResultAssertions());
	}

	@NotNull
	DslHttpSampler customerSearchViaAmperityId() {
		return httpSampler(currentTestMethodName(), customerSearchUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(AMPERITY_ID, "${" + AMPERITY_ID + "}")
			.children(customerSearchResultAssertions());
	}

	@NotNull
	DslHttpSampler customerSearchViaAccount() {
		return httpSampler(currentTestMethodName(), customerSearchUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(ACCOUNT_TYPE, "${" + ACCOUNT_TYPE + "}")
			.param(ACCOUNT_ID, "${" + ACCOUNT_ID + "}")
			.children(customerSearchResultAssertions());
	}

	@NotNull
	DslHttpSampler overrideSearchViaGuestId() {
		return httpSampler(currentTestMethodName(), overrideRequestUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(CUSTOMER_ID, "${" + CUSTOMER_ID + "}")
			.children(overrideSearchViaGuestIdResultAssertions());
	}

	@NotNull
	DslHttpSampler overrideSearchViaRequestorId() {
		return httpSampler(currentTestMethodName(), overrideRequestUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(USER_ID, "${" + USER_ID + "}")
			.children(overrideSearchRequestorIdResultAssertions());
	}

	@NotNull
	DslHttpSampler overrideSearchViaStatus() {
		return httpSampler(currentTestMethodName(), overrideRequestUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(OVERRIDE_STATUS, "${" + OVERRIDE_STATUS + "}")
			.children(overrideSearchStatusResultAssertions());
	}

	@NotNull
	DslHttpSampler overrideSearchViaCategory() {
		return httpSampler(currentTestMethodName(), overrideRequestUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(OVERRIDE_CATEGORY, "${" + OVERRIDE_CATEGORY + "}")
			.children(overrideSearchCategoryResultAssertions());
	}

	@NotNull
	DslHttpSampler overrideSearchViaGuestIdAndStatus() {
		return httpSampler(currentTestMethodName(), overrideRequestUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(CUSTOMER_ID, "${" + CUSTOMER_ID + "}")
			.param(OVERRIDE_STATUS, "${" + OVERRIDE_STATUS + "}")
			.param(OVERRIDE_CATEGORY, "${" + OVERRIDE_CATEGORY + "}")
			.children(overrideSearchGuestIdAndStatusResultAssertions());
	}

	@NotNull
	DslHttpSampler overrideSearchViaMultipleInputs() {
		return httpSampler(currentTestMethodName(), overrideRequestUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(CUSTOMER_ID, "${" + CUSTOMER_ID + "}")
			.param(OVERRIDE_STATUS, "${" + OVERRIDE_STATUS + "}")
			.param(OVERRIDE_CATEGORY, "${" + OVERRIDE_CATEGORY + "}")
			.param(USER_ID, "${" + USER_ID + "}")
			.children(overrideSearchMultipleInputsResultAssertions());
	}

	@NotNull
	DslHttpSampler overrideStatusViaGuestId() {
		return httpSampler(currentTestMethodName(), overrideStatusUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(GUEST_ID, "${" + GUEST_ID + "}")
			.children(overrideStatusResultAssertions());
	}

	@NotNull
	DslHttpSampler overrideDetail() {
		return httpSampler(currentTestMethodName(), overrideDetailUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param("changeId", "${changeId}")
			.param("overrideType", "${overrideType}")
			.children(httpResponseCodeAssertion(200),
			          jsonExtendedAssertion(MSG_ASSERT_GUEST_ID, JP_GUEST_ID, "${" + GUEST_ID + "}"),
			          jsonExtendedAssertion("assert status", "$.status", "${status}"),
			          jsonExtendedAssertion("assert created date", "$.createdDate", "${createdDate}"));
	}

	@NotNull
	DslHttpSampler rankedData() {
		return httpSampler(currentTestMethodName(), rankedDataUrl)
			.method(GET)
			.header(HEADER_AUTHORIZATION, DEF_AUTH_VAR)
			.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
			.param(GUEST_ID, "${" + GUEST_ID + "}")
			.children(rankedDataResultAssertions());
	}

	private SamplerChild[] rankedDataResultAssertions() {
		return new SamplerChild[]{
			httpResponseCodeAssertion(200),
			jsonExtendedAssertion("assert guest id", JP_GUEST_ID, "${" + GUEST_ID + "}"),
			jsonExtendedAssertion("has name", "$.names[*].accountId", "${hasName}").compareAsArrayLength(),
			jsonExtendedAssertion("has address", "$.addresses[*].accountId", "${hasAddress}").compareAsArrayLength(),
			jsonExtendedAssertion("has phone", "$.phoneNumbers[*].accountId", "${hasPhone}").compareAsArrayLength(),
			jsonExtendedAssertion("has email", "$.emailAddresses[*].accountId", "${hasEmail}").compareAsArrayLength(),
			jsonExtendedAssertion("has birth date", "$.dateOfBirths[*].accountId", "${hasBirthDate}")
				.compareAsArrayLength()
		};
	}

	@NotNull
	private SamplerChild[] customerSearchResultAssertions() {
		String jpBase = "$.searchResults[0].";
		return new SamplerChild[]{
			httpResponseCodeAssertion(200),

			// assert resultCount, actualResultCount (should be same as resultCount), mLifeId, numberOfAccounts, rewards tier
			jsonExtendedAssertion("assert result count", "$.resultCount", "${expects}"),
			jsonExtendedAssertion("assert result count", "$.actualResultCount", "${expects}"),
			jsonExtendedAssertion("assert number of accounts", jpBase + NUM_OF_ACCOUNTS, "${" + NUM_OF_ACCOUNTS + "}"),
			jsonExtendedAssertion(MSG_ASSERT_MLIFE_ID, jpBase + MLIFE_ID, "${" + MLIFE_ID + "}"),
			jsonExtendedAssertion(MSG_ASSERT_AMPERITY_ID, jpBase + AMPERITY_ID, "${" + AMPERITY_ID + "}"),
			jsonExtendedAssertion(MSG_ASSERT_GUEST_ID, jpBase + GUEST_ID, "${" + GUEST_ID + "}"),
			jsonExtendedAssertion(MSG_ASSERT_REWARDS_TIER, jpBase + REWARDS_TIER, "${" + REWARDS_TIER + "}")
		};
	}

	@NotNull
	private SamplerChild[] customerInsightsResultAssertions() {
		String jpGlobalM11y = "$.customerInfo.globalMarketabilityFlags.";
		return new SamplerChild[]{
			httpResponseCodeAssertion(200),

			jsonExtendedAssertion("assert result message", "$.resultMessage", "null"),
			jsonExtendedAssertion(MSG_ASSERT_GUEST_ID, "$.customerInfo.identifiers." + GUEST_ID, "${" + GUEST_ID + "}"),
			jsonExtendedAssertion(MSG_ASSERT_MLIFE_ID, "$.customerInfo.loyalty.mLifeID", "${" + MLIFE_ID + "}"),
			jsonExtendedAssertion(MSG_ASSERT_REWARDS_TIER, "$.customerInfo.loyalty.mLifeTierCurrent",
			                      "${" + REWARDS_TIER + "}"),
			jsonExtendedAssertion(MSG_ASSERT_STATE_CODE, "$.customerInfo.accountInfo." + STATE_CD,
			                      "${" + STATE_CD + "}"),
			jsonExtendedAssertion("assert marketable name", jpGlobalM11y + MARKETABLE_NAME,
			                      "${" + MARKETABLE_NAME + "}"),
			jsonExtendedAssertion("assert marketable address", jpGlobalM11y + MARKETABLE_ADDR,
			                      "${" + MARKETABLE_ADDR + "}"),
			jsonExtendedAssertion("assert valid email", jpGlobalM11y + IS_VALID_EMAIL, "${" + IS_VALID_EMAIL + "}"),
			jsonExtendedAssertion("assert valid mail", jpGlobalM11y + IS_VALID_MAIL, "${" + IS_VALID_MAIL + "}"),
			jsonExtendedAssertion("assert global opt out", jpGlobalM11y + GLOBAL_OPT_OUT, "${" + GLOBAL_OPT_OUT + "}"),
			jsonExtendedAssertion("assert BetMGM enrollment", jpGlobalM11y + BETMGM_ENROLL, "${" + BETMGM_ENROLL + "}")
		};
	}

	@NotNull
	private SamplerChild[] customerInsightsByAccountAssertions() {
		return new SamplerChild[]{
			httpResponseCodeAssertion(200),

			jsonExtendedAssertion("assert result message", "$.resultMessage", "null"),
			jsonExtendedAssertion(MSG_ASSERT_GUEST_ID, "$.customerInfo.identifiers." + GUEST_ID, "${" + GUEST_ID + "}"),
			jsonExtendedAssertion(MSG_ASSERT_MLIFE_ID, "$.customerInfo.loyalty.mLifeID", "${" + MLIFE_ID + "}"),
			jsonExtendedAssertion(MSG_ASSERT_REWARDS_TIER, "$.customerInfo.loyalty.mLifeTierCurrent",
			                      "${" + REWARDS_TIER + "}"),
			jsonExtendedAssertion(MSG_ASSERT_STATE_CODE, "$.customerInfo.accountInfo." + STATE_CD,
			                      "${" + STATE_CD + "}")
		};
	}

	@NotNull
	private SamplerChild[] customerSearchViaUnSurvivedPhoneResultAssertions() {
		return new SamplerChild[]{httpResponseCodeAssertion(200)};
	}

	@NotNull
	private SamplerChild[] overrideSearchViaGuestIdResultAssertions() {
		String jpBase = "$.results[0].";
		return new SamplerChild[]{
			httpResponseCodeAssertion(200),
			jsonExtendedAssertion(MSG_ASSERT_CUSTOMER_ID, jpBase + CUSTOMER_ID, "${" + CUSTOMER_ID + "}"),
			jsonExtendedAssertion(MSG_ASSERT_CUSTOMER_ID, jpBase + GUEST_NAME, "${" + GUEST_NAME + "}")
				.compareStringIgnoreCase(),
			};
	}

	@NotNull
	private SamplerChild[] overrideSearchRequestorIdResultAssertions() {
		String jpBase = "$.results[0].";
		return new SamplerChild[]{
			httpResponseCodeAssertion(200),
			jsonExtendedAssertion(MSG_ASSERT_REQUESTOR_ID, jpBase + USER_ID, "${" + USER_ID + "}"),
			jsonExtendedAssertion("assert valid guest name", jpBase + REQUESTOR_NAME, "${" + REQUESTOR_NAME + "}"),
			};
	}

	@NotNull
	private SamplerChild[] overrideSearchStatusResultAssertions() {
		return new SamplerChild[]{
			httpResponseCodeAssertion(200),
			jsonExtendedAssertion(MSG_ASSERT_OVERRIDE_STATUS, "$.results[0]." + OVERRIDE_STATUS,
			                      "${" + OVERRIDE_STATUS + "}")
		};
	}

	@NotNull
	private SamplerChild[] overrideSearchCategoryResultAssertions() {
		return new SamplerChild[]{
			httpResponseCodeAssertion(200),
			jsonExtendedAssertion(MSG_ASSERT_OVERRIDE_CATEGORY, "$.results[0]." + OVERRIDE_CATEGORY,
			                      "${" + OVERRIDE_CATEGORY + "}")
		};
	}

	@NotNull
	private SamplerChild[] overrideStatusResultAssertions() {
		return new SamplerChild[]{
			httpResponseCodeAssertion(200),
			jsonExtendedAssertion(MSG_ASSERT_GUEST_ID, JP_GUEST_ID, "${" + GUEST_ID + "}"),
			jsonExtendedAssertion("assert PII status",
			                      "$.pii[?(@.status=='Active' && @.entity=='${piiEntity}')].entity",
			                      "${piiEntity}").compareContains(),
			jsonExtendedAssertion("assert Marketability status",
			                      "$.marketability[?(@.status=='Active' && @.entity=='${marketabilityEntity}')].entity",
			                      "${marketabilityEntity}").compareContains()
		};
	}

	private SamplerChild[] overrideSearchGuestIdAndStatusResultAssertions() {
		String jpBase = "$.results[0].";
		return new SamplerChild[]{
			httpResponseCodeAssertion(200),
			jsonExtendedAssertion(MSG_ASSERT_CUSTOMER_ID, jpBase + CUSTOMER_ID, "${" + CUSTOMER_ID + "}"),
			jsonExtendedAssertion(MSG_ASSERT_CUSTOMER_ID, jpBase + GUEST_NAME,
			                      "${" + GUEST_NAME + "}").compareStringIgnoreCase(),
			jsonExtendedAssertion(MSG_ASSERT_OVERRIDE_STATUS, jpBase + OVERRIDE_STATUS, "${" + OVERRIDE_STATUS + "}"),
			};
	}

	@NotNull
	private SamplerChild[] overrideSearchMultipleInputsResultAssertions() {
		String jpBase = "$.results[0].";
		return new SamplerChild[]{
			httpResponseCodeAssertion(200),
			jsonExtendedAssertion(MSG_ASSERT_CUSTOMER_ID, jpBase + CUSTOMER_ID, "${" + CUSTOMER_ID + "}"),
			jsonExtendedAssertion(MSG_ASSERT_CUSTOMER_ID, jpBase + GUEST_NAME,
			                      "${" + GUEST_NAME + "}").compareStringIgnoreCase(),
			jsonExtendedAssertion(MSG_ASSERT_OVERRIDE_STATUS, jpBase + OVERRIDE_STATUS, "${" + OVERRIDE_STATUS + "}"),
			jsonExtendedAssertion(MSG_ASSERT_REQUESTOR_ID, jpBase + USER_ID, "${" + USER_ID + "}"),
			jsonExtendedAssertion("assert valid guest name", jpBase + REQUESTOR_NAME, "${" + REQUESTOR_NAME + "}"),
			jsonExtendedAssertion(MSG_ASSERT_OVERRIDE_CATEGORY, jpBase + OVERRIDE_CATEGORY,
			                      "${" + OVERRIDE_CATEGORY + "}")
		};
	}

	private String resolveApiUrl(String uriPath) {
		boolean useApigee = setting(KEY_USE_APIGEE, true);
		return (useApigee ? setting(KEY_APIGEE_HOST) : setting(KEY_AZURE_HOST)) + uriPath;
	}
}