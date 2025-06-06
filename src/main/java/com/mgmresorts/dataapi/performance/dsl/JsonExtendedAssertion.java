package com.mgmresorts.dataapi.performance.dsl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.collections.HashTree;
import us.abstracta.jmeter.javadsl.core.BuildTreeContext;
import us.abstracta.jmeter.javadsl.core.assertions.DslAssertion;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsr223PostProcessor.PostProcessorVars;
import us.abstracta.jmeter.javadsl.core.testelements.BaseTestElement;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
import static us.abstracta.jmeter.javadsl.core.postprocessors.DslJsonExtractor.JsonQueryLanguage.JSON_PATH;

public class JsonExtendedAssertion extends BaseTestElement implements DslAssertion {
	private static int jsonPropIndex = 1;
	private final String path;
	private final String value;
	private boolean compareAsNumber;
	private boolean compareAsInt;
	private boolean compareAsArrayLength;
	private boolean compareStringIgnoreCase;
	private boolean compareContains;

	public JsonExtendedAssertion(String name, String jsonPath, String value) {
		super(name, null);
		this.path = jsonPath;
		this.value = value;
	}

	public JsonExtendedAssertion compareStringIgnoreCase() {
		this.compareAsNumber = false;
		this.compareAsArrayLength = false;
		this.compareStringIgnoreCase = true;
		return this;
	}

	public JsonExtendedAssertion compareAsNumber() {
		this.compareAsNumber = true;
		this.compareAsArrayLength = false;
		return this;
	}

	public JsonExtendedAssertion compareAsInt() {
		this.compareAsNumber = true;
		this.compareAsInt = true;
		this.compareAsArrayLength = false;
		return this;
	}

	public JsonExtendedAssertion compareAsArrayLength() {
		this.compareAsNumber = false;
		this.compareAsInt = true;
		this.compareAsArrayLength = true;
		return this;
	}

	public JsonExtendedAssertion compareContains() {
		this.compareAsNumber = false;
		this.compareAsInt = false;
		this.compareAsArrayLength = false;
		this.compareContains = true;
		return this;
	}

	@SuppressWarnings("java:S2696")
	@Override
	public HashTree buildTreeUnder(HashTree parent, BuildTreeContext context) {
		String varName = name + " var_" + (jsonPropIndex++);
		jsonExtractor(varName, path).queryLanguage(JSON_PATH).defaultValue("null").buildTreeUnder(parent, context);

		if (compareContains) {
			jsr223PostProcessor(name + " adjust EXPECTED for null value", s -> {
				String realVar = StringUtils.substringBetween(value, "${", "}");
				if (StringUtils.isEmpty(s.vars.get(realVar))) { s.vars.put(realVar, "null"); }
			}).buildTreeUnder(parent, context);

			jsr223PostProcessor(name + " adjust ACTUAL for null value", s -> {
				String val = s.vars.get(varName);
				if (StringUtils.isEmpty(val)) { s.vars.put(varName, "null"); }
			}).buildTreeUnder(parent, context);

			return responseAssertion(name).scopeVariable(varName)
			                              .containsSubstrings(value)
			                              .buildTreeUnder(parent, context);
		}

		if (compareStringIgnoreCase) {
			jsr223PostProcessor(name + " ready for string ignore case assertion", s -> {
				String realVar = StringUtils.substringBetween(value, "${", "}");
				String val = s.vars.get(realVar);
				if (!val.equals("null")) {
					s.vars.put(realVar, StringUtils.lowerCase(val));
				} else {
					s.vars.put(realVar, "null");
				}
			}).buildTreeUnder(parent, context);

			jsr223PostProcessor(name + " ready for string ignore case assertion", s -> {
				String val = s.vars.get(varName);
				if (!val.equals("null")) {
					s.vars.put(varName, StringUtils.lowerCase(val));
				} else {
					s.vars.put(varName, "null");
				}
			}).buildTreeUnder(parent, context);
		}

		if (compareAsNumber) {
			jsr223PostProcessor(name + " ready for numeric assertion", s -> {
				storeAsFormattedNumber(s, varName);
				storeVarAsFormattedNumber(s);
			}).buildTreeUnder(parent, context);
		}

		if (compareAsArrayLength) {
			jsr223PostProcessor(name + " ready for array length assertion", s -> {
				String expectedVal = s.vars.get(StringUtils.substringBetween(value, "${", "}"));
				String actualVal = s.vars.get(varName);
				int matchCount = NumberUtils.toInt(StringUtils.defaultIfBlank(s.vars.get(varName + "_matchNr"), "0"));
				if (NumberUtils.isCreatable(expectedVal)) {
					s.vars.put(varName, actualVal != null ? "" + matchCount : "0");
					storeVarAsFormattedNumber(s);
				} else {
					// assume boolean
					s.vars.put(varName, "" + (actualVal != null && matchCount > 0));
				}
			}).buildTreeUnder(parent, context);
		}

		return responseAssertion(name).scopeVariable(varName).equalsToStrings(value).buildTreeUnder(parent, context);
	}

	@Override
	protected TestElement buildTestElement() {
		// Since we have already overwritten buildUnderTree this method will actually not be invoked
		return null;
	}

	private void storeVarAsFormattedNumber(PostProcessorVars s) {
		if (value != null && value.matches("^\\$\\{.+}$")) {
			String expectedVar = this.value.substring(2, this.value.length() - 1);
			storeAsFormattedNumber(s, expectedVar);
		}
	}

	private void storeAsFormattedNumber(PostProcessorVars script, String varName) {
		String val = script.vars.get(varName);
		if (NumberUtils.isCreatable(val)) {
			double numValue = NumberUtils.toDouble(val);
			if (compareAsInt) {
				script.vars.put(varName, String.valueOf((int) numValue));
			} else {
				script.vars.put(varName, String.valueOf(numValue));
			}
		}
	}
}