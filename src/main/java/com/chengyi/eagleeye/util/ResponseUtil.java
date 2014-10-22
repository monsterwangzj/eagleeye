/*
 * Created on Dec 8, 2007
 */
package com.chengyi.eagleeye.util;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONException;

/**
 * @author wangzhaojun
 */
public class ResponseUtil {

	public static void sendMessageNoCache(HttpServletResponse response, String message) {
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0);
		response.setHeader("Pragma", "no-cache");
		StringBuffer sb = new StringBuffer();
		try {
//			response.setContentLength(message.length());
			java.io.PrintWriter writer = response.getWriter();
			sb.append(message);
			writer.write(sb.toString());
			response.flushBuffer();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new JSONException();
		}
	}

	public static void sendMessageWithCache(HttpServletResponse response, String message) {
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Expires", DateUtil.formatGMT(new Date(System.currentTimeMillis() + 600000)));
		response.setHeader("Cache-Control", "public");
		response.setHeader("Cache-Control", "max-age=600");
		// response.setHeader("Pragma", "public");
		StringBuffer sb = new StringBuffer();
		try {
			response.setContentLength(message.length());
			java.io.PrintWriter writer = response.getWriter();
			sb.append(message);
			writer.write(sb.toString());
			response.flushBuffer();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new JSONException();
		}
	}

	public static void sendMessageWithCache(HttpServletResponse response, String message, int cacheMilliSeconds) {
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Expires", DateUtil.formatGMT(new Date(System.currentTimeMillis() + cacheMilliSeconds)));
		response.setHeader("Cache-Control", "public");
		response.setHeader("Cache-Control", "max-age=" + cacheMilliSeconds / 1000);
		// response.setHeader("Pragma", "public");
		StringBuffer sb = new StringBuffer();
		try {
			response.setContentLength(message.length());
			java.io.PrintWriter writer = response.getWriter();
			sb.append(message);
			writer.write(sb.toString());
			response.flushBuffer();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new JSONException();
		}
	}

	public static void sendXmlMessageNoCache(HttpServletResponse response, String message) {
		response.setContentType("text/xml;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0);
		response.setHeader("Pragma", "no-cache");
		StringBuffer sb = new StringBuffer();
		try {
			response.setContentLength(message.getBytes("UTF-8").length);
			java.io.PrintWriter writer = response.getWriter();
			sb.append(message);
			writer.write(sb.toString());
			response.flushBuffer();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void sendXmlMessageWithCache(HttpServletResponse response, String message) {
		response.setContentType("text/xml;charset=UTF-8");
		response.setHeader("Expires", DateUtil.formatGMT(new Date(System.currentTimeMillis() + 300000)));
		response.setHeader("Cache-Control", "public");
		response.setHeader("Cache-Control", "max-age=300");
		// response.setHeader("Pragma", "public");

		StringBuffer sb = new StringBuffer();
		try {

			java.io.PrintWriter writer = response.getWriter();
			sb.append(message);
			writer.write(sb.toString());
			response.flushBuffer();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void sendXmlMessageWithCache(HttpServletResponse response, String message, int cacheMilliSeconds) {
		response.setContentType("text/xml;charset=UTF-8");
		response.setHeader("Expires", DateUtil.formatGMT(new Date(System.currentTimeMillis() + cacheMilliSeconds)));
		response.setHeader("Cache-Control", "public");
		response.setHeader("Cache-Control", "max-age=" + cacheMilliSeconds / 1000);
		// response.setHeader("Pragma", "public");

		StringBuffer sb = new StringBuffer();
		try {
			java.io.PrintWriter writer = response.getWriter();
			sb.append(message);
			writer.write(sb.toString());
			response.flushBuffer();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void setCacheHeader(HttpServletResponse response, int cacheAge) {
		response.setHeader("Pragma", "Public");
		// HTTP 1.1
		response.setHeader("Cache-Control", "max-age=" + cacheAge);
		response.setDateHeader("Expires", System.currentTimeMillis() + cacheAge * 1000L);
	}

	public static void setNoCacheHeader(HttpServletResponse response) {
		// HTTP 1.0
		response.setHeader("Pragma", "No-cache");
		// HTTP 1.1
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
	}

}
