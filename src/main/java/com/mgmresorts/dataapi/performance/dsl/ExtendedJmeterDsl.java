package com.mgmresorts.dataapi.performance.dsl;

import us.abstracta.jmeter.javadsl.core.assertions.DslJsonAssertion;
import us.abstracta.jmeter.javadsl.core.assertions.DslResponseAssertion;

import static us.abstracta.jmeter.javadsl.JmeterDsl.jsonAssertion;
import static us.abstracta.jmeter.javadsl.JmeterDsl.responseAssertion;
import static us.abstracta.jmeter.javadsl.core.assertions.DslResponseAssertion.TargetField.RESPONSE_CODE;
import static us.abstracta.jmeter.javadsl.core.postprocessors.DslJsonExtractor.JsonQueryLanguage.JSON_PATH;

public class ExtendedJmeterDsl {

	private ExtendedJmeterDsl() { }

	public static JsonExtendedAssertion jsonExtendedAssertion(String name, String jsonPath, String expected) {
		return new JsonExtendedAssertion(name, jsonPath, expected);
	}

	public static DslJsonAssertion jsonStringAssertion(String label, String jsonPath, String expected) {
		return jsonAssertion(label, jsonPath).queryLanguage(JSON_PATH).equalsTo("\"" + expected + "\"");
	}

	public static DslJsonAssertion jsonNumberAssertion(String label, String jsonPath, String expected) {
		return jsonAssertion(label, jsonPath).queryLanguage(JSON_PATH).equalsTo(expected);
	}

	public static JsonExtendedAssertion jsonArrayLengthAssertion(String label, String jsonPath, String expected) {
		return jsonExtendedAssertion(label, jsonPath, expected).compareAsArrayLength();
	}

	public static DslResponseAssertion httpResponseCodeAssertion(int responseCode) {
		return responseAssertion("expects HTTP " + responseCode)
			.fieldToTest(RESPONSE_CODE)
			.equalsToStrings(String.valueOf(responseCode));
	}
}