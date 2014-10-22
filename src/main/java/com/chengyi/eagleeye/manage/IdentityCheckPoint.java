package com.chengyi.eagleeye.manage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.chengyi.eagleeye.model.User;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.CookieUtil;
import com.chengyi.eagleeye.util.XXTEA;

public class IdentityCheckPoint {
	private static transient final Logger logger = Logger.getLogger(IdentityCheckPoint.class);
	
	private static final String PRODUCT_NAME = "monitor_ck";
	private static final String XXT_KEY = "U!S@G*&R^~ku6&K%E(Ycom)T";
	private static final String seperator = "|";
	
	public static void setIdentity(HttpServletResponse response, User user) {
		String cookieName = PRODUCT_NAME;
		try {
			StringBuilder cookieValue = new StringBuilder()
					.append(user.getId()).append(seperator)
					.append(user.getEmail() == null ? "" : user.getEmail()).append(seperator)
					.append(URLEncoder.encode(user.getName(), ApplicaRuntime.UTF8_ENCODING)).append(seperator)
					.append(URLEncoder.encode(XXTEA.encrypt(user.getId() + seperator + System.currentTimeMillis(), XXT_KEY), ApplicaRuntime.UTF8_ENCODING));
			// uid|email|username|xxtea(uid|timestamp)
			CookieUtil.getInstance().setCookie(response, cookieName, cookieValue.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getUsernameFromCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = CookieUtil.getInstance().getCookie(request, PRODUCT_NAME);
		if (cookie != null) {
			String value = cookie.getValue();
			if (StringUtils.isNotEmpty(value)) {
				String[] arr = value.split(CookieUtil.seperator);
				if (arr != null && arr.length == 4) {
					String username = arr[2];
					if (username != null) {
						try {
							return URLDecoder.decode(username, ApplicaRuntime.UTF8_ENCODING);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// get uid failed, remove monitor_ck cookie
		removeMonitorCookie(request, response);
		return null;
	}

	public static Long getUidFromCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = CookieUtil.getInstance().getCookie(request, PRODUCT_NAME);
		if (cookie != null) {
			String value = cookie.getValue();
			if (StringUtils.isNotEmpty(value)) {
				String[] arr = value.split(CookieUtil.seperator);
				if (arr != null && arr.length == 4) {
					String uidStr = arr[0];
					String encryptionStr = arr[3];
					String srcStr = XXTEA.decrypt(encryptionStr, XXT_KEY);
					String[] srcStrArr = srcStr.split(CookieUtil.seperator);
					if (srcStrArr != null && srcStrArr.length >= 2) {
						if (srcStrArr[0].equals(uidStr)) {
							try {
								return Long.parseLong(uidStr);
							} catch (NumberFormatException nfe) {
								nfe.printStackTrace();
							}
						}
					}
				}
			}
		}

		// get uid failed, remove monitor_ck cookie
		removeMonitorCookie(request, response);
		return null;
	}
	
	public static boolean removeMonitorCookie(HttpServletRequest request, HttpServletResponse response) {
		CookieUtil cu = CookieUtil.getInstance();
		Cookie cookie = cu.getCookie(request, PRODUCT_NAME);
		cu.deleteCookie(response, cookie, null, null);
		if (logger.isDebugEnabled()) {
			logger.debug("remove cookie: " + PRODUCT_NAME);
		}
		return true;
	}

	public static boolean removeIdentity(HttpServletRequest request, HttpServletResponse response) {
		CookieUtil cu = CookieUtil.getInstance();
		Cookie cookie = cu.getCookie(request, PRODUCT_NAME);

		cookie = cu.getCookie(request, PRODUCT_NAME);
		cu.deleteCookie(response, cookie, null, null);

		if (logger.isDebugEnabled()) {
			logger.debug("remove cookie: " + PRODUCT_NAME);
		}
		return true;
	}

}
