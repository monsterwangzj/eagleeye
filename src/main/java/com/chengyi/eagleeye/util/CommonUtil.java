package com.chengyi.eagleeye.util;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.util.JSONUtils;

import org.apache.log4j.Logger;

public class CommonUtil {
	private static Logger logger = Logger.getLogger(CommonUtil.class);

	public final static String XXT_VIDEO_KEY = "!@#^~ku6&%(com)T";

	public static int pageSize = 10;

	public static enum StatEntity {
		video, playlist, user
	};

	public static enum StatDataType {
		object, playcount, refercount
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("REMOTE-HOST");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static Long getLong(String longStr) {
		Long longval = null;
		try {
			longval = Long.parseLong(longStr);
		} catch (NumberFormatException nfe) {
			logger.debug("error:" + nfe.getMessage());
		}
		return longval;
	}

	public static Double get2pDouble(Double f) {
		if (f == null) return null;
		BigDecimal b = new BigDecimal(f);
		double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}
	
	public static Double getNpDouble(Double f, int point) {
		if (f == null) return null;
		BigDecimal b = new BigDecimal(f);
		double f1 = b.setScale(point, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}
	
	public static Integer getInteger(String longStr) {
		Integer intval = null;
		try {
			intval = Integer.parseInt(longStr);
		} catch (NumberFormatException nfe) {
			logger.debug("error:" + nfe.getMessage());
		}
		return intval;
	}

	public static Long getPage(String longStr) {
		Long page = getLong(longStr);
		if (page == null)
			return 1L;
		else
			return page;
	}

	public static long getTotalPage(long totalCount, long pageSize) {
		return (totalCount % pageSize == 0) ? (totalCount / pageSize) : (totalCount / pageSize + 1);
	}

	public static String quote(String str) {
		String jsonText = JSONUtils.quote(str);
		if (jsonText.length() > 2) {
			jsonText = jsonText.substring(1, jsonText.length() - 1);
		}
		return jsonText;
	}
	
	/** 
	  * 替换一个字符串中的某些指定字符 
	  * @param strData String 原始字符串 
	  * @param regex String 要替换的字符串 
	  * @param replacement String 替代字符串 
	  * @return String 替换后的字符串 
	  */  
	 public static String replaceString(String strData, String regex,  
	         String replacement)  
	 {  
	     if (strData == null)  
	     {  
	         return null;  
	     }  
	     int index;  
	     index = strData.indexOf(regex);  
	     String strNew = "";  
	     if (index >= 0)  
	     {  
	         while (index >= 0)  
	         {  
	             strNew += strData.substring(0, index) + replacement;  
	             strData = strData.substring(index + regex.length());  
	             index = strData.indexOf(regex);  
	         }  
	         strNew += strData;  
	         return strNew;  
	     }  
	     return strData;  
	 }  
	  
	 /** 
	  * 替换字符串中特殊字符 
	  */  
	public static String encodeXmlString(String strData)  
	 {  
	     if (strData == null)  
	     {  
	         return "";  
	     }  
	     strData = replaceString(strData, "&", "&amp;");  
	     strData = replaceString(strData, "<", "&lt;");  
	     strData = replaceString(strData, ">", "&gt;");  
	     strData = replaceString(strData, "&apos;", "&apos;");  
	     strData = replaceString(strData, "'", ""); 
	     strData = replaceString(strData, "\"", "");  
	     strData = replaceString(strData, "$", "");  
	     strData = replaceString(strData, "^", "");  
	     strData = replaceString(strData, "%", "");  
	     strData = replaceString(strData, "#", "");  
	     
	     strData = stripNonValidXMLCharacters(strData);
	     return strData;  
	 }  
	  
	 /** 
	  * 还原字符串中特殊字符 
	  */  
	public static String decodeString(String strData)  
	 {  
	     strData = replaceString(strData, "&lt;", "<");  
	     strData = replaceString(strData, "&gt;", ">");  
	     strData = replaceString(strData, "&apos;", "&apos;");  
	     strData = replaceString(strData, "&quot;", "\"");  
	     strData = replaceString(strData, "&amp;", "&");  
	     return strData;  
	 } 
	
	public static String stripNonValidXMLCharacters(String in) {
	    StringBuffer out = new StringBuffer(); // Used to hold the output.
	    char current; // Used to reference the current character.

	    if (in == null || ("".equals(in)))
	        return ""; // vacancy test.
	    for (int i = 0; i < in.length(); i++) {
	        current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught
	                                // here; it should not happen.
	        if ((current == 0x9) || (current == 0xA) || (current == 0xD)
	                || ((current >= 0x20) && (current <= 0xD7FF))
	                || ((current >= 0xE000) && (current <= 0xFFFD))
	                || ((current >= 0x10000) && (current <= 0x10FFFF)))
	            out.append(current);
	    }
	    return out.toString();
	}
	
	public static String toMultiLine(String str, int len) {
        char[] chs = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for(int i = 0, sum = 0; i < chs.length; i++) {
            sum += chs[i] < 0xff ? 1 : 2;
            sb.append(chs[i]);
            if(sum >= len) {
                sum = 0;
                sb.append("<br/>");
            }
        }
        return sb.toString();
    }
	
}
