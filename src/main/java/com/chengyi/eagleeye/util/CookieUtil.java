package com.chengyi.eagleeye.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * cookie utility class
 * 
 * @author wangzhaojun
 */
public class CookieUtil {
	private static transient final Logger LOG = Logger.getLogger(CookieUtil.class);

	private String monitorDomain;

	public static final String seperator = "[|]";
	
	private static CookieUtil instance = null;

	private CookieUtil() {
	}

	public static CookieUtil getInstance() {
		if (instance == null) {
			synchronized (CookieUtil.class) {
				if (instance == null)
					instance = new CookieUtil();
			}
		}
		return instance;
	}

	public void setCookie(HttpServletResponse response, String name, String value) {
//		setCookie(response, name, value, null, null, -1);
		setCookie(response, name, value, null, null, 14 * 24 * 3600);
	}

	public void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
		setCookie(response, name, value, null, null, maxAge);
	}

	public void setCookie(HttpServletResponse response, String name, String value, String domain, String path,
			int maxAge) {
		if (name == null || value == null) {
			LOG.warn("name or value can't be null, name=" + name + " value=" + value);
			return;
		}

		Cookie cookie = new Cookie(name, value);
		cookie.setSecure(false);

		if (domain == null) {
			domain = monitorDomain;
		}
		cookie.setDomain(domain);

		path = (path == null ? "/" : path);
		cookie.setPath(path);
		cookie.setMaxAge(maxAge);

		response.addCookie(cookie);
	}

	public Cookie[] getCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		return cookies;
	}

	public Cookie getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		Cookie returnCookie = null;
		if (cookies == null) {
			return returnCookie;
		}
		for (int i = 0; i < cookies.length; i++) {
			Cookie thisCookie = cookies[i];
			if (thisCookie.getName().equals(name)) {
				if (!thisCookie.getValue().equals("")) {
					returnCookie = thisCookie;
					break;
				}
			}
		}
		return returnCookie;
	}

	public String getCookieValue(HttpServletRequest request, String name) {
		Cookie cookie = getCookie(request, name);
		if (cookie == null) {
			return null;
		}
		return cookie.getValue();
	}

	public void deleteCookie(HttpServletResponse response, Cookie cookie, String domain, String path) {
		if (cookie != null) {
			// Delete the cookie by setting its maximum age to zero
			cookie.setMaxAge(0);

			if (LOG.isDebugEnabled()) {
				LOG.debug("deleteCookie(HttpServletResponse, Cookie) -  : cookie=" + cookie);
			}

			if (domain == null) {
				domain = monitorDomain;
			}
			cookie.setDomain(domain);

			path = (path == null ? "/" : path);
			cookie.setPath(path);

			response.addCookie(cookie);
		}
	}

	public String getMonitorDomain() {
		return monitorDomain;
	}

	public void setMonitorDomain(String monitorDomain) {
		this.monitorDomain = monitorDomain;
	}

}
