package com.mgmresorts.dataapi.performance.cdpapi;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.mgmresorts.dataapi.performance.utils.EncryptionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.mgmresorts.dataapi.performance.cdpapi.Constants.*;
import static com.mgmresorts.dataapi.performance.cdpapi.TestHelper.*;
import static com.mgmresorts.dataapi.performance.utils.TemplateHelper.replace;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.tuple.Pair.of;

/**
 * generate data from CosmosDB for performance testing
 */
public class CosmosDataGenerator extends BasePerfLibrary {

	private static final String KEY_TEST_DATA = ".testData";
	private static final String KEY_GEN_QUERY = ".generator.query";
	private static final String KEY_GEN_HEADERS = ".generator.headers";
	private static final String KEY_GEN_DB = ".generator.db";
	private static final String KEY_UNSURVIVED_DB = "customerSearchViaUnsurvivedPhone.generator.db";
	private static final String KEY_UNSURVIVED_PHONE_MULTI_QUERY =
		"customerSearchViaUnsurvivedPhone.multiResult.generator.query";
	private static final String KEY_UNSURVIVED_PHONE_HEADERS = "customerSearchViaUnsurvivedPhone.generator.headers";
	private static final String KEY_UNSURVIVED_PHONE_SINGLE_QUERY =
		"customerSearchViaUnsurvivedPhone.singleResult.generator.query";
	private static final String KEY_SURVIVORSHIP_KEY = "generator.cosmos.survivorship.key";
	private static final String KEY_SURVIVORSHIP_ENDPOINT = "generator.cosmos.survivorship.endpoint";
	private static final String KEY_OVERRIDE_KEY = "generator.cosmos.override.key";
	private static final String KEY_OVERRIDE_ENDPOINT = "generator.cosmos.override.endpoint";

	interface EnrichRow {
		void enrichRow(int rowIndex, Map row);
	}

	@NotNull
	private String customerSearchViaGuestId() throws IOException {
		String key = currentTestMethodName();
		return queryToFile(
			newSurvivorshipCosmosBuilder(),
			setting(key + KEY_GEN_DB),
			replace(setting(key + KEY_GEN_QUERY), of(OFFSET, randomizeOffset())),
			csvHeadersAsList(setting(key + KEY_GEN_HEADERS)),
			setting(OUTPUT_BASE) + "/" + setting(key + KEY_TEST_DATA),
			null);
	}

	@NotNull
	private String customerInsightsViaGuestId() throws IOException {
		String key = currentTestMethodName();
		CosmosClientBuilder builder = newSurvivorshipCosmosBuilder();
		String dbConfig = setting(key + KEY_GEN_DB);
		String dbName = StringUtils.substringBefore(dbConfig, ".");

		try (CosmosClient cosmosClient = builder.buildClient()) {
			CosmosContainer m11yContainer =
				cosmosClient.getDatabase(dbName).getContainer(setting(key + ".generator.collection2"));
			String output = setting(OUTPUT_BASE) + "/" + setting(key + KEY_TEST_DATA);
			String innerQuery = setting(key + ".generator.query2");

			List<String> headers = csvHeadersAsList(setting(key + KEY_GEN_HEADERS));
			return queryToFile(
				builder,
				dbConfig,
				replace(setting(key + KEY_GEN_QUERY), of(OFFSET, randomizeOffset())),
				headers,
				output,
				(rowIndex, rowData) -> {
					String query1 = replace(innerQuery, of(GUEST_ID, rowData.get(GUEST_ID)));
					logInnerQuery(rowIndex, query1);
					m11yContainer.queryItems(query1, new CosmosQueryRequestOptions(), Map.class).stream().findFirst()
					             .ifPresent(row -> headers.forEach(header -> {
						             Object rowValue = row.get(header);
						             if (rowValue != null) { rowData.put(header, String.valueOf(rowValue)); }
					             }));
				}
			);
		}
	}

	@NotNull
	private String customerInsightsViaAccount() throws IOException { return getAccountAndGuestIds(); }

	@NotNull
	private String customerSearchViaAccount() throws IOException { return getAccountAndGuestIds(); }

	@NotNull
	private String getAccountAndGuestIds() throws IOException {
		CosmosClientBuilder builder = newSurvivorshipCosmosBuilder();

		String key = secondTestMethodName();
		String dbConfig = setting(key + KEY_GEN_DB);
		String dbName = StringUtils.substringBefore(dbConfig, ".");

		try (CosmosClient cosmosClient = builder.buildClient()) {
			CosmosContainer cpcContainer =
				cosmosClient.getDatabase(dbName).getContainer(setting(key + ".generator.collection2"));

			String output = setting(OUTPUT_BASE) + "/" + setting(key + KEY_TEST_DATA);

			String innerQuery = setting(key + ".generator.query2");

			List<String> headers = csvHeadersAsList(setting(key + KEY_GEN_HEADERS));
			return queryToFile(
				builder,
				dbConfig,
				replace(setting(key + KEY_GEN_QUERY), of(OFFSET, randomizeOffset())),
				headers,
				output,
				(rowIndex, rowData) -> {
					if (rowData.containsKey(SOURCES)) {
						Object sources = rowData.get(SOURCES);
						if (sources instanceof List<?> sourceList) {
							// remove onetrust... no bueno
							sourceList.removeIf(source -> source instanceof Map sourceMap &&
							                              StringUtils.equals(String.valueOf(sourceMap.get(SOURCE_ID)),
							                                                 "272"));

							// randomly pick one source
							int sourceIndex = RandomUtils.nextInt(0, sourceList.size());
							Object account = sourceList.get(sourceIndex);
							if (account instanceof Map<?, ?> accountMap) {
								rowData.put(ACCOUNT_TYPE, TestHelper.toAccountType((int) accountMap.get(SOURCE_ID)));
								rowData.put(ACCOUNT_ID, accountMap.get(ACCOUNT));
								rowData.remove(SOURCES);

								Object guestId = String.valueOf(rowData.get(GUEST_ID));
								String query1 = replace(innerQuery, of(GUEST_ID, guestId));
								logInnerQuery(rowIndex, query1);
								cpcContainer.queryItems(query1, new CosmosQueryRequestOptions(), Map.class).stream()
								            .findFirst()
								            .ifPresent(row -> headers.forEach(header -> {
									            Object rowValue = row.get(header);
									            if (rowValue != null) { rowData.put(header, String.valueOf(rowValue)); }
								            }));

								// check minimum data requirements
								if (!rowData.containsKey(MLIFE_ID) || !rowData.containsKey(REWARDS_TIER)) {
									// invalid test data, throw away
									rowData.clear();
								}

								// good data found... use it!
								return;
							}
						}
					}

					// no good; don't use
					rowData.clear();
				}
			);
		}
	}

	@NotNull
	private String customerSearchViaUnsurvivedPhoneMultiResults() throws IOException {
		String key = currentTestMethodName();
		return queryToFile(
			newSurvivorshipCosmosBuilder(),
			setting(KEY_UNSURVIVED_DB),
			replace(setting(KEY_UNSURVIVED_PHONE_MULTI_QUERY), of(OFFSET, randomizeOffset())),
			csvHeadersAsList(setting(KEY_UNSURVIVED_PHONE_HEADERS)),
			setting(OUTPUT_BASE) + "/" + setting(key + KEY_TEST_DATA),
			null);
	}

	@NotNull
	private String customerSearchViaUnsurvivedPhoneSingleResult() throws IOException {
		String key = currentTestMethodName();
		return queryToFile(
			newSurvivorshipCosmosBuilder(),
			setting(KEY_UNSURVIVED_DB),
			replace(setting(KEY_UNSURVIVED_PHONE_SINGLE_QUERY), of(OFFSET, 0)),
			csvHeadersAsList(setting(KEY_UNSURVIVED_PHONE_HEADERS)),
			setting(OUTPUT_BASE) + "/" + setting(key + KEY_TEST_DATA),
			null);
	}

	@NotNull
	private String overrideSearchViaGuestId() throws IOException {
		String key = currentTestMethodName();
		return queryToFile(
			newOverrideCosmosBuilder(),
			setting(key + KEY_GEN_DB),
			replace(setting(key + KEY_GEN_QUERY), of(OFFSET, randomizeOffset())),
			csvHeadersAsList(setting(key + KEY_GEN_HEADERS)),
			setting(OUTPUT_BASE) + "/" + setting(key + KEY_TEST_DATA),
			null);
	}

	@NotNull
	private String overrideStatusViaGuestId() throws IOException {
		String key = currentTestMethodName();

		CosmosClientBuilder survivorshipBuilder = newSurvivorshipCosmosBuilder();
		String dbAndCollection = setting(key + ".generator.db3");
		String survivorshipDbName = StringUtils.substringBefore(dbAndCollection, ".");
		String cpcCollection = StringUtils.substringAfter(dbAndCollection, ".");
		CosmosContainer cpcContainer = survivorshipBuilder.buildClient()
		                                                  .getDatabase(survivorshipDbName)
		                                                  .getContainer(cpcCollection);
		String queryExists = setting(key + ".generator.query3");

		CosmosClientBuilder builder = newOverrideCosmosBuilder();
		String dbName = StringUtils.substringBefore(setting(key + KEY_GEN_DB), ".");
		try (CosmosClient cosmosClient = builder.buildClient()) {
			CosmosContainer m11yContainer = cosmosClient.getDatabase(dbName)
			                                            .getContainer(setting(key + ".generator.collection2"));
			String output = setting(OUTPUT_BASE) + "/" + setting(key + KEY_TEST_DATA);
			String queryM11y = setting(key + ".generator.query2");

			List<String> headers = csvHeadersAsList(setting(key + KEY_GEN_HEADERS));
			return queryToFile(
				builder,
				setting(key + KEY_GEN_DB),
				replace(setting(key + KEY_GEN_QUERY), of(OFFSET, randomizeOffset())),
				headers,
				output,
				(rowIndex, rowData) -> {
					Object guestId = rowData.get(GUEST_ID);
					String queryGuestIdExits = replace(queryExists, of(GUEST_ID, guestId));
					logInnerQuery(rowIndex, queryGuestIdExits);
					cpcContainer.queryItems(queryGuestIdExits, new CosmosQueryRequestOptions(), Map.class).stream()
					            .findFirst().ifPresent(map -> {
						            if (NumberUtils.toInt(Objects.toString(map.get("count"), "0")) < 1) {
							            // no corresponding guest id in survivorship db
							            rowData.clear();
							            rowData.put(GUEST_ID, guestId);
						            } else {
							            String query1 = replace(queryM11y, of(GUEST_ID, guestId));
							            logInnerQuery(rowIndex, query1);
							            m11yContainer.queryItems(query1, new CosmosQueryRequestOptions(), Map.class)
							                         .stream().findFirst().ifPresent(row -> headers.forEach(header -> {
								                         Object rowValue = row.get(header);
								                         if (rowValue != null) {
									                         rowData.put(header, String.valueOf(rowValue));
								                         }
							                         }));
						            }
					            });
				}
			);
		}
	}

	@NotNull
	private String overrideDetail() throws IOException {
		String key = currentTestMethodName();
		return queryToFile(
			newOverrideCosmosBuilder(),
			setting(key + KEY_GEN_DB),
			replace(setting(key + KEY_GEN_QUERY), of(OFFSET, randomizeOffset())),
			csvHeadersAsList(setting(key + KEY_GEN_HEADERS)),
			setting(OUTPUT_BASE) + "/" + setting(key + KEY_TEST_DATA),
			(rowIndex, rowData) -> {
				String changeId = Objects.toString(rowData.get("changeId"), "");
				if (StringUtils.isNotBlank(changeId)) { rowData.put("changeId", EncryptionUtils.encrypt(changeId)); }
			});
	}

	@NotNull
	private String rankedData() throws IOException {
		String key = currentTestMethodName();

		List<String> headers = csvHeadersAsList(setting(key + KEY_GEN_HEADERS));

		String db2 = setting(key + ".generator.db2");
		String database2 = StringUtils.substringBefore(db2, ".");
		String container2 = StringUtils.substringAfterLast(db2, ".");
		String query2 = setting(key + ".generator.query2");

		CosmosClientBuilder survivorship = newSurvivorshipCosmosBuilder();
		CosmosContainer innerContainer = survivorship.buildClient().getDatabase(database2).getContainer(container2);

		return queryToFile(
			survivorship,
			setting(key + KEY_GEN_DB),
			replace(setting(key + KEY_GEN_QUERY), of(OFFSET, randomizeOffset())),
			headers,
			setting(OUTPUT_BASE) + "/" + setting(key + KEY_TEST_DATA),
			((rowIndex, row) -> {
				String query1 = replace(query2, of(GUEST_ID, String.valueOf(row.get(GUEST_ID))));
				logInnerQuery(rowIndex, query1);

				innerContainer.queryItems(query1, new CosmosQueryRequestOptions(), Map.class).stream().findFirst()
				              .ifPresent(row2 -> headers.forEach(header -> {
					              Object rowValue = row2.get(header);
					              if (rowValue != null) { row.put(header, String.valueOf(rowValue)); }
				              }));
			}));
	}

	private CosmosClientBuilder newSurvivorshipCosmosBuilder() {
		String key = setting(KEY_SURVIVORSHIP_KEY);
		if (isBlank(key)) {
			throw new IllegalArgumentException("No key or managed identity configured; Unable to connect to CosmosDB");
		}

		return new CosmosClientBuilder().key(key).endpoint(setting(KEY_SURVIVORSHIP_ENDPOINT)).gatewayMode();
	}

	private CosmosClientBuilder newOverrideCosmosBuilder() {
		String key = setting(KEY_OVERRIDE_KEY);
		if (isBlank(key)) {
			throw new IllegalArgumentException("No key or managed identity configured; Unable to connect to CosmosDB");
		}

		return new CosmosClientBuilder().key(key).endpoint(setting(KEY_OVERRIDE_ENDPOINT)).gatewayMode();
	}

	@NotNull
	private String queryToFile(CosmosClientBuilder builder,
	                           String db,
	                           String query,
	                           List<String> fields,
	                           String output,
	                           EnrichRow enricher)
		throws IOException {

		String database = substringBefore(db, ".");
		String container = substringAfter(db, ".");

		// 1. connect to cosmos
		try (CosmosClient client = builder.buildClient()) {
			CosmosDatabase cosmosDb = client.getDatabase(database);
			CosmosContainer cpc = cosmosDb.getContainer(container);

			// 2. execute query (random "start from" and configured size)
			logger.info("executing query: {}", query);
			AtomicReference<Integer> rowIndex = new AtomicReference<>(0);
			List<String> data = cpc.queryItems(query, new CosmosQueryRequestOptions(), Map.class).stream()
			                       .map(row -> {
				                       if (enricher != null) {
					                       enricher.enrichRow(rowIndex.updateAndGet(num -> num + 1), row);
				                       }
				                       if (MapUtils.isEmpty(row)) { return null; }
				                       return fields.stream()
				                                    .map(field -> String.valueOf(defaultIfNull(row.get(field), "")))
				                                    .collect(Collectors.joining(","));
			                       })
			                       .filter(Objects::nonNull)
			                       .toList();

			// 3. save result to file
			File writeTo = new File(output);
			String fileContent = String.join(",", fields) + "\n" + String.join("\n", data);
			logger.info("writing data to file: {}", writeTo.getAbsolutePath());
			FileUtils.writeStringToFile(writeTo, fileContent, UTF_8);
			return writeTo.getAbsolutePath();
		}
	}

	@NotNull
	private static List<String> csvHeadersAsList(String headers) { return Arrays.asList(split(headers, ",")); }

	private static int randomizeOffset() { return RandomUtils.nextInt(0, 54321); }

	private void logInnerQuery(int rowIndex, String query) {
		logger.info("[{}] executing inner query: {}", rowIndex, query);
	}

	@SuppressWarnings("java:S2629")
	public static void main(String[] args) throws Exception {
		logBanner("Cosmos Data Generator", "");

		CosmosDataGenerator gen = new CosmosDataGenerator();

		// test data for customer search
		gen.logger.info("\n\tcustomerInsightsViaAccount data file created: {}", gen.customerSearchViaAccount());
		gen.logger.info("\n\tcustomerSearchViaGuestId data file created: {}", gen.customerSearchViaGuestId());
		gen.logger.info("\n\tcustomerSearchViaUnsurvivedPhoneSingleResult data file created: {}",
		                gen.customerSearchViaUnsurvivedPhoneSingleResult());
		gen.logger.info("\n\tcustomerSearchViaUnsurvivedPhoneMultiResults data file created: {}",
		                gen.customerSearchViaUnsurvivedPhoneMultiResults());

		// test data for customer insights
		gen.logger.info("\n\tcustomerInsightsViaGuestId data file created: {}", gen.customerInsightsViaGuestId());
		gen.logger.info("\n\tcustomerInsightsViaAccount data file created: {}", gen.customerInsightsViaAccount());

		// test data for override search
		gen.logger.info("\n\toverrideSearchViaGuestId data file created: {}", gen.overrideSearchViaGuestId());

		// dest data for override status
		gen.logger.info("\n\toverrideStatusViaGuestId data file created: {}", gen.overrideStatusViaGuestId());
		gen.logger.info("\n\toverrideDetail data file created: {}", gen.overrideDetail());

		// dest data for ranked data list
		gen.logger.info("\n\trankedData data file created: {}", gen.rankedData());
	}
}
