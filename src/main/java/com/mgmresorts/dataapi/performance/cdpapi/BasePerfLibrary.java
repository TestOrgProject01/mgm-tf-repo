package com.mgmresorts.dataapi.performance.cdpapi;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static com.mgmresorts.dataapi.performance.cdpapi.TestHelper.getSettings;

abstract class BasePerfLibrary {
	private static final String REF = "ref:";

	protected final Logger logger = LogManager.getLogger(getClass());
	protected final Map<String, String> settings = getSettings();

	protected String setting(String key) { return resolveRef(settings.getOrDefault(key, null)); }

	protected int setting(String key, int defaultValue) {
		if (settings.containsKey(key)) {
			String value = resolveRef(settings.get(key));
			if (NumberUtils.isDigits(value)) { return NumberUtils.toInt(value); }
		}
		return defaultValue;
	}

	protected double setting(String key, Double defaultValue) {
		if (settings.containsKey(key)) {
			String value = resolveRef(settings.get(key));
			if (NumberUtils.isParsable(value)) { return NumberUtils.toDouble(value); }
		}
		return defaultValue;
	}

	protected boolean setting(String key, boolean defaultValue) {
		return settings.containsKey(key) ? BooleanUtils.toBoolean(resolveRef(settings.get(key))) : defaultValue;
	}

	private String resolveRef(String value) {
		if (StringUtils.isBlank(value)) { return value; }
		return StringUtils.startsWith(value, REF) ?
		       settings.getOrDefault(StringUtils.substringAfter(value, REF), "") : value;
	}
}
