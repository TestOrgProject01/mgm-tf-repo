package com.mgmresorts.dataapi.performance.cdpapi;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mgmresorts.dataapi.performance.cdpapi.Constants.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class TestHelper {
	private static final String TITLE_LINE_TOP = "\n" + StringUtils.repeat("-", 80) + "\n| PERF-TEST :: ";
	private static final String TITLE_LINE_BOTTOM = "\n" + StringUtils.repeat("-", 80);
	private static final String SETTINGS_FILE = "perf.properties";

	private static final Logger LOGGER = LoggerFactory.getLogger(TestHelper.class);

	private static final Map<String, String> SETTINGS = loadSettings();
	private static final Map<Integer, String> ACCOUNT_TYPES = initAccountTypes();

	private TestHelper() { }

	@NotNull
	public static String currentTestMethodName() { return new Throwable().getStackTrace()[1].getMethodName(); }

	public static String secondTestMethodName() { return new Throwable().getStackTrace()[2].getMethodName(); }

	@SuppressWarnings("java:S106")
	public static void printTestTitle() {
		String nameofCurrMethod = new Throwable().getStackTrace()[1].getMethodName();
		System.out.println(TITLE_LINE_TOP + nameofCurrMethod + TITLE_LINE_BOTTOM);
	}

	public static void logBanner(String title, String subTitle) {
		if (LOGGER.isInfoEnabled()) {
			String divider = StringUtils.repeat("=", 80);
			title = StringUtils.center(RegExUtils.replaceAll(StringUtils.upperCase(title), "(.)", "$1 "), 74);
			LOGGER.info("\n{}\n:: {} ::\n{}\n>> executing: {}\n",
			            divider,
			            title,
			            divider,
			            subTitle);
		}
	}

	@SuppressWarnings("java:S106")
	@NotNull
	public static String collectInput(String prompt) {
		System.out.print(prompt);
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();
		if (StringUtils.isBlank(input)) {
			System.err.println("\nInvalid input, please try again.\n");
			return collectInput(prompt);
		} else {
			return input;
		}
	}

	public static Map<String, String> getSettings() {
		if (MapUtils.isEmpty(SETTINGS)) { SETTINGS.putAll(loadSettings()); }
		return SETTINGS;
	}

	public static String toAccountType(int accountTypeId) { return ACCOUNT_TYPES.getOrDefault(accountTypeId, ""); }

	/**
	 * Loads the settings from the appropriate settings.properties file.
	 *
	 * @return a map containing the loaded settings
	 */
	@NotNull
	private static Map<String, String> loadSettings() {
		Properties settingsProps = new Properties();

		// 1. determine the right settings.properties to use
		String userSettings = System.getProperty("user.home") + "/.cdpapi/" + SETTINGS_FILE;
		String projectSettings = "/" + SETTINGS_FILE;

		// try user specific version
		Path userSettingsFile = Path.of(userSettings);
		if (Files.exists(userSettingsFile)) {
			// 2.1 load user specific settings
			LOGGER.info("Load settings from user specific settings.properties file: {}", userSettings);
			try (InputStream settingsStream = Files.newInputStream(userSettingsFile)) {
				settingsProps.load(settingsStream);
			} catch (IOException e) {
				LOGGER.error("Failed to load from user-specific file " + userSettings + ": " + e.getMessage(), e);
				System.exit(1);
			}
		} else {
			Class<?> clazz = TestHelper.class;
			final URL resource = clazz.getResource(projectSettings);
			if (resource != null) {
				// 2.2 load project specific settings
				LOGGER.info("Load settings from project specific settings.properties file: {}", projectSettings);
				try (InputStream settingsStream = clazz.getResourceAsStream(projectSettings)) {
					settingsProps.load(settingsStream);
				} catch (IOException e) {
					LOGGER.error("Failed to load from project-specific file " + projectSettings + ": " + e.getMessage(),
					             e);
					System.exit(1);
				}
			} else {
				LOGGER.error("No {} found in classpath or in user home directory! Exiting...", SETTINGS_FILE);
				System.exit(1);
			}
		}

		Map<String, String> settings = new HashMap<>();
		settingsProps.forEach((k, v) -> settings.put((String) k, (String) v));

		injectEnvVars(settings);

		// 3. load environment specific settings
		String env = config(KEY_ENV);
		// maybe it's in the settings.properties
		if (StringUtils.isBlank(env)) {
			env = settings.getOrDefault(KEY_ENV, DEF_ENV);
		} else {
			// make sure it goes in the settings for future use
			settings.put(KEY_ENV, env);
		}

		switch (StringUtils.lowerCase(env)) {
			case "dev" -> envSettingsForDev(settings);
			case "qa" -> envSettingsForQa(settings);
			case "preprod" -> envSettingsForPreprod(settings);
			case "prod" -> envSettingsForProd(settings);
			default -> {
				LOGGER.error("Invalid environment specified: {}", env);
				System.exit(1);
			}
		}

		// 4. ready for use
		return settings;
	}

	private static void injectEnvVars(Map<String, String> settings) {
		String envPath = SystemUtils.getUserHome().getAbsolutePath() + "/.cdpapi/.env";
		if (Files.isReadable(Paths.get(envPath))) {
			Pattern regexEnvLine = Pattern.compile("^(export )?([^=]+)=(.+)$");
			try {
				FileUtils.readLines(new File(envPath), UTF_8).forEach(line -> {
					line = StringUtils.trim(line);
					if (StringUtils.isBlank(line) || StringUtils.startsWith(line, "#")) { return; }

					Matcher matcher = regexEnvLine.matcher(line);
					if (!matcher.matches()) { return; }

					String key = StringUtils.trim(matcher.group(2));
					String value = StringUtils.trim(StringUtils.unwrap(matcher.group(3), "\""));
					settings.put(key, value);
				});
			} catch (IOException e) {
				LOGGER.error("Unable to read/parse " + envPath + ": " + e.getMessage(), e);
			}
		}
	}

	@NotNull
	private static String config(String key) {
		return StringUtils.isBlank(key) ? "" : SystemUtils.getEnvironmentVariable(key, System.getProperty(key, ""));
	}

	private static void envSettingsForDev(@NotNull Map<String, String> settings) {
		settings.put(KEY_AZURE_HOST, "https://cdpapi-uw-fa-d.azurewebsites.net/api/c360/v1");
		settings.put(KEY_APIGEE_HOST, "https://dev-api.apigee.devtest.vegas/c360/v2");
		settings.put(KEY_GRAPHQL_HOST, "https://mgm-nonprod-qa4.apigee.net/graphql");

		settings.put(KEY_BFF_API_KEY, settings.get("BFF_API_KEY_DEV"));

		envSettingsForNonProd(settings);
	}

	private static void envSettingsForQa(@NotNull Map<String, String> settings) {
		settings.put(KEY_AZURE_HOST, "https://cdpapi-uw-fa-q.azurewebsites.net/api/c360/v1");
		settings.put(KEY_APIGEE_HOST, "https://qa2-api.apigee.devtest.vegas/c360/v2");
		settings.put(KEY_GRAPHQL_HOST, "https://mgm-nonprod-qa4.apigee.net/graphql/graphql");

		settings.put(KEY_BFF_API_KEY, settings.get("BFF_API_KEY_QA"));

		envSettingsForNonProd(settings);
	}

	private static void envSettingsForPreprod(@NotNull Map<String, String> settings) {
		settings.put(KEY_AZURE_HOST, "https://cdpapi-uw-fa-r.azurewebsites.net/api/c360/v1");
		settings.put(KEY_APIGEE_HOST, "https://preprod-api.apigee.devtest.vegas/c360/v2");
		settings.put(KEY_GRAPHQL_HOST, "https://mgm-nonprod-preprod.apigee.net/graphql");

		settings.put(KEY_BFF_API_KEY, settings.get("BFF_API_KEY_PREPROD"));

		envSettingsForNonProd(settings);
	}

	private static void envSettingsForNonProd(@NotNull Map<String, String> settings) {
		settings.put("IDENTITY_URL", "https://azdeapi-dev.mgmresorts.com/int/identity/authorization/v1/mgmsvc/token");

		settings.put("CUSTOMER_DATA_PRODUCT_SERVICES_SECRET", settings.get("CUSTOMER_DATA_PRODUCT_SERVICES_NONPROD"));
		settings.put("AVAYA_SECRET", settings.get("AVAYA_NONPROD"));
		settings.put("BACKSTAGE_SECRET", settings.get("BACKSTAGE_NONPROD"));
		settings.put("BETMGM_SECRET", settings.get("BETMGM_NONPROD"));
		settings.put("BOOKING_SECRET", settings.get("BOOKING_NONPROD"));
		settings.put("CYBER_DEFENSE_SECRET", settings.get("CYBER_DEFENSE_NONPROD"));
		settings.put("MGM_IDENTITY_SERVICE_SECRET", settings.get("MGM_IDENTITY_SERVICE_NONPROD"));
		settings.put("MGM_RTC_SERVICE_SECRET", settings.get("MGM_RTC_SERVICE_NONPROD"));
		settings.put("PAYMENT_SECRET", settings.get("PAYMENT_NONPROD"));
		settings.put("RECOMMENDATION_SECRET", settings.get("RECOMMENDATION_NONPROD"));
		settings.put("RHYTMOS_RCX_MCP_SECRET", settings.get("RHYTMOS_RCX_MCP_NONPROD"));
		settings.put("SEVENROOMS_SECRET", settings.get("SEVENROOMS_NONPROD"));

		// assume we are using the same Azure credential and ALT resource to run perf. testing
		// SP-cigdataapi-nonprod
		settings.put("AZURE_LOAD_TESTING_CREDS", settings.get("SP_CIGDATAAPI_NONPROD_TENANT_ID") + ":" +
		                                         settings.get("SP_CIGDATAAPI_NONPROD_CLIENT_ID") + ":" +
		                                         settings.get("SP_CIGDATAAPI_NONPROD_CLIENT_SECRET"));

		settings.put("AZURE_LOAD_TESTING_SUBSCRIPTION", "3429a2df-1acc-4e8a-858b-58d3d612a7b0");
		settings.put("AZURE_LOAD_TESTING_RESOURCE_GROUP", "cigdataapi-uw-rg-d");
		settings.put("AZURE_LOAD_TESTING_RESOURCE", "cigrataapi-uw-lt-d");
	}

	private static void envSettingsForProd(@NotNull Map<String, String> settings) {
		settings.put(KEY_AZURE_HOST, "https://cdpapi-uw-fa-p.azurewebsites.net/api/c360/v1");
		settings.put(KEY_APIGEE_HOST, "https://api.apigee.mgmresorts.com/c360/v2");
		settings.put(KEY_GRAPHQL_HOST, "https://mgm-prod-prod.apigee.net/graphql");

		settings.put(KEY_BFF_API_KEY, settings.get("BFF_API_KEY_PROD"));

		settings.put("IDENTITY_URL", "https://azdeapi.mgmresorts.com/identity/authorization/v1/mgmsvc/token");

		settings.put("AVAYA_SECRET", settings.get("AVAYA_PROD"));
		settings.put("BACKSTAGE_SECRET", settings.get("BACKSTAGE_PROD"));
		settings.put("BETMGM_SECRET", settings.get("BETMGM_PROD"));
		settings.put("CUSTOMER_DATA_PRODUCT_SERVICES_SECRET", settings.get("CUSTOMER_DATA_PRODUCT_SERVICES_PROD"));
		settings.put("CYBER_DEFENSE_SECRET", settings.get("CYBER_DEFENSE_PROD"));
		settings.put("MGM_IDENTITY_SERVICE_SECRET", settings.get("MGM_IDENTITY_SERVICE_PROD"));
		settings.put("MGM_RTC_SERVICE_SECRET", settings.get("MGM_RTC_SERVICE_PROD"));
		settings.put("RHYTMOS_RCX_MCP_SECRET", settings.get("RHYTMOS_RCX_MCP_PROD"));
		settings.put("SEVENROOMS_SECRET", settings.get("SEVENROOMS_NONPROD"));
	}

	private static Map<Integer, String> initAccountTypes() {
		Map<Integer, String> accountTypes = new HashMap<>();
		accountTypes.put(1, "patron");
		accountTypes.put(2, "opera");
		accountTypes.put(17, "archtics");
		accountTypes.put(253, "sevenrooms");
		accountTypes.put(272, "onetrust");
		accountTypes.put(280, "borgata");
		accountTypes.put(281, "borgata_ticketing");
		accountTypes.put(283, "customers");
		accountTypes.put(284, "amperity");
		accountTypes.put(285, "guest");
		accountTypes.put(598, "betmgm");
		accountTypes.put(601, "tcolv_lms");
		accountTypes.put(602, "tcolv_patron");
		return accountTypes;
	}


}
