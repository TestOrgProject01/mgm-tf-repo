package com.mgmresorts.dataapi.performance.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public enum TemplateHelper {
	;

	@SafeVarargs
	public static String replace(String template, Pair<String, Object>... replacements) {
		if (StringUtils.isBlank(template)) { return template; }
		if (replacements == null || replacements.length == 0) { return template; }

		String result = template;
		for (Pair<String, Object> replacement : replacements) {
			result = result.replace("{" + replacement.getKey() + "}",
			                        String.valueOf(defaultIfNull(replacement.getValue(), "")));
		}

		return result;
	}
}
