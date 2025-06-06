package com.mgmresorts.dataapi.performance.utils;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public enum EncryptionUtils {
	;

	private static final String KEY = "y0r_s3cRet1s**s@fE**";
	private static final String SIG = "$ntrt$:";
	private static final StandardPBEStringEncryptor ENCRYPTOR = initEncryptor();

	@SuppressWarnings("java:S6437")
	private static StandardPBEStringEncryptor initEncryptor() {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(KEY);
		encryptor.setStringOutputType("hexadecimal");
		return encryptor;
	}

	public static String encrypt(String text) {
		if (StringUtils.isBlank(text)) { return text; }
		if (StringUtils.startsWith(text, SIG)) { return text; }
		return SIG + ENCRYPTOR.encrypt(text);
	}

	public static String decrypt(String text) {
		if (StringUtils.isBlank(text)) { return text; }
		if (!StringUtils.startsWith(text, SIG)) { return text; }
		try {
			return ENCRYPTOR.decrypt(StringUtils.substringAfter(text, SIG));
		} catch (RuntimeException e) {
			return text;
		}
	}
}