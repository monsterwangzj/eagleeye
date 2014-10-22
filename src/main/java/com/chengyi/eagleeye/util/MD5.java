package com.chengyi.eagleeye.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MD5 {

	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	private static Log log = LogFactory.getLog(MD5.class);

	public MD5() {
	}

	public static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return (new StringBuilder()).append(hexDigits[d1]).append(hexDigits[d2]).toString();
	}

	public static String MD5Encode(String origin, String encodingType) {
		String resultString = null;
		MessageDigest md = null;
		try {
			resultString = new String(origin);
			md = MessageDigest.getInstance("MD5");
			
		} catch (NoSuchAlgorithmException e) {
			log.error(e);
		}
		
		try {
			md.update(origin.getBytes(encodingType));
		} catch (UnsupportedEncodingException e) {
			md.update(origin.getBytes());
		}
		
		resultString = byteArrayToHexString(md.digest());
		return resultString;
	}

}