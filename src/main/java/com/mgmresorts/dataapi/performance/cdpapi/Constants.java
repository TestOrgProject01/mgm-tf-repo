package com.mgmresorts.dataapi.performance.cdpapi;

enum Constants {
	;

	static final String KEY_AUTH_TOKEN = "AUTH_TOKEN";
	static final String KEY_AZURE_HOST = "AZURE_HOST";
	static final String KEY_APIGEE_HOST = "APIGEE_HOST";
	static final String KEY_GRAPHQL_HOST = "GRAPHQL_HOST";
	static final String KEY_BFF_API_KEY = "BFF_API_KEY";
	static final String KEY_ENV = "perf.env";

	static final String DEF_ENV = "dev";
	static final String OUTPUT_BASE = "generator.cosmos.survivorship.outputBase";
	static final String OVERRIDE_OUTPUT_BASE = "generator.cosmos.override.outputBase";

	// headers
	static final String HEADER_X_FUNCTION_KEY = "x-functions-key";
	static final String CONTENT_TYPE_JSON = "application/json";

	// request fields
	static final String FIRST_NAME = "firstName";
	static final String MIDDLE_NAME = "middleName";
	static final String LAST_NAME = "lastName";
	static final String ADDRESS_LINE1 = "addressLineOne";
	static final String ADDRESS_LINE2 = "addressLineTwo";
	static final String CITY = "city";
	static final String STATE_CD = "stateCd";
	static final String POSTAL_CD = "postalCd";
	static final String DATE_OF_BIRTH = "dateOfBirth";
	static final String EMAIL_ADDRESS = "emailAddress";
	static final String COUNTRY_CD = "countryCd";
	static final String GENDER = "gender";
	static final String PHONE_NUMBER = "phoneNumber";
	static final String SKIP = "skip";
	static final String TOP = "top";
	static final String DISPLAY_RESTRICTION = "displayRestriction";
	static final String PAGE = "page";
	static final String SIZE = "size";
	static final String DIRECTION = "dir";
	static final String SORT = "sort";
	static final String EXACT_NAME = "exactNameSearch";
	static final String ACTIVE_CUSTOMER = "activeCustomers";
	static final String NUM_OF_ACCOUNTS = "numberOfAccounts";

	static final String LAST_GAMING_VISIT_CORPORATE = "lastGamingVisitCorporate";
	static final String LAST_NON_GAMING_VISIT_CORPORATE = "lastNonGamingVisitCorporate";
	static final String HOST_FIRST_NAME = "hostFirstName";
	static final String HOST_LAST_NAME = "hostLastName";
	static final String MLIFE_TIER = "mLifeTierCurrent";
	static final String LINKED_ACCOUNTS = "linkedAccounts";

	static final String MARKETABLE_NAME = "marketableNameFlag";
	static final String MARKETABLE_ADDR = "marketableAddressFlag";
	static final String IS_VALID_EMAIL = "isValidEmail";
	static final String IS_VALID_MAIL = "isValidMail";
	static final String GLOBAL_OPT_OUT = "goo";
	static final String BETMGM_ENROLL = "betMGMEnrollment";
	static final String ONLY_UNSURVIVED = "onlyUnSurvived";

	static final String OFFSET = "offset";
	static final String CUSTOMER_ID = "customerId";
	static final String USER_ID = "userId";

	static final String GUEST_ID = "guestId";
	static final String AMPERITY_ID = "amperityId";
	static final String SEVENROOMS_ID = "sevenRoomsId";
	static final String MLIFE_ID = "mLifeId";
	static final String REWARDS_TIER = "mgmRewardsTierCurrent";
	static final String SOURCES = "sources";
	static final String SOURCE_ID = "sourceId";
	static final String ACCOUNT = "account";
	static final String ACCOUNT_ID = "accountId";
	static final String ACCOUNT_TYPE = "accountType";
	static final String GUEST_NAME = "guestName";
	static final String REQUESTOR_NAME = "requestorName";
	static final String OVERRIDE_STATUS = "status";
	static final String OVERRIDE_CATEGORY = "changeRequestType";
}
