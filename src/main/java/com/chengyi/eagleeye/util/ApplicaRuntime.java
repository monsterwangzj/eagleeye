package com.chengyi.eagleeye.util;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.net.URLCodec;

import net.sf.json.JSONObject;

import com.chengyi.eagleeye.model.DataCenterServer;
import com.chengyi.eagleeye.model.Item;

/**
 * @author wangzhaojun
 * 
 */
public class ApplicaRuntime {
	public static List<Item> itemList = null;
	
	public static DataCenterServer dcServer = null;
	public static String containerPath = null;

	public static String serverDomain = "localhost";
	public static int port = 80;
	public static String globalFlag = NetUtil.getLocalIp() + "2.3.0";

	/**
	 * 报警方式定义
	 */
	public static final byte ALARMCHANNEL_SMS = (byte) 1;
	public static final byte ALARMCHANNEL_EMAIL = (byte) 2;
	public static final byte ALARMCHANNEL_YOUNI = (byte) 4;

	// 是否发送短信报警
	public static boolean isSendSms = true;
	public static String osName = System.getProperties().getProperty("os.name");
	public static String UTF8_ENCODING = "UTF-8";
	
	public static void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String encodeURI(String uri) {
		try {
			return URLEncoder.encode(uri, UTF8_ENCODING);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uri;
	}
	
	public static String encodeURI2(String uri) {
		try {
			uri = uri.replaceAll("\\|", "%7C");
			uri = uri.replaceAll("\\^", "%5E");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uri;
	}

	public static String responseJSON(int status, String data, HttpServletResponse response) {
		try {
			JSONObject json = new JSONObject();
			json.put("status", status);
			json.put("data", data);
			ResponseUtil.sendMessageNoCache(response, json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean containsAlphabit(String string) {
		return Pattern.compile("(?i)[a-z]").matcher(string).find();
	}

	public static String getDayBeforeDate(String day, int beforeDays, String format) {
		SimpleDateFormat sd = new SimpleDateFormat(format);
		Date date;
		try {
			date = sd.parse(day);
			
			Calendar cdate = Calendar.getInstance();
			cdate.setTime(date);
			cdate.add(Calendar.DATE, -beforeDays);
			return new SimpleDateFormat("yyyyMMdd").format(cdate.getTime()) + "00";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getLastNDaysStartDate(int n) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, -n);
		return new SimpleDateFormat("yyyyMMdd").format(now.getTime()) + "00";
	}

	public static String getLastNDaysEndDate(int n) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, -(n - 1));
		return new SimpleDateFormat("yyyyMMdd").format(now.getTime()) + "00";
	}

	public static int daysDiff(String startTime, String endTime, String format) {
		// 按照传入的格式生成一个simpledateformate对象
		SimpleDateFormat sd = new SimpleDateFormat(format);
		long nd = 1000 * 24 * 60 * 60; // 一天的毫秒数
//		long nh = 1000 * 60 * 60; // 一小时的毫秒数
//		long nm = 1000 * 60; // 一分钟的毫秒数
//		long ns = 1000; // 一秒钟的毫秒数
		long diff;
		long day = 0L;
		try {
			// 获得两个时间的毫秒时间差异
			diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
			day = diff / nd;// 计算差多少天
//			long hour = diff % nd / nh;// 计算差多少小时
//			long min = diff % nd % nh / nm;// 计算差多少分钟
//			long sec = diff % nd % nh % nm / ns;// 计算差多少秒
			// 输出结果
//			System.out.println("时间相差：" + day + "天" + hour + "小时" + min + "分钟" + sec + "秒。");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return (int) day;
	}
	
	public static String timeDiff(String startTime, String endTime, String format) {
		SimpleDateFormat sd = new SimpleDateFormat(format);
		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		long ns = 1000;
		long diff;
		
		String result = "";
		try {
			diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
			
			long day = diff / nd;// 计算差多少天
			long hour = diff % nd / nh;// 计算差多少小时
			long min = diff / nm;
			long sec = diff % nd % nh % nm / ns;
			
			if (day > 0) {
				result += day + "天";
			}
			
			if (hour > 0) {
				result += hour + "小时";
			}
			
			if (min > 0) {
				result += min + "分";
			}
			
			if (sec > 0) {
				result += sec + "秒";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String fuzzyTimeDiff(long startTime, long endTime) { // time unit: ms
		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		long ns = 1000;
		long diff;

		String result = "";
		diff = endTime - startTime;

		long day = diff / nd;// 计算差多少天
		long hour = diff % nd / nh;// 计算差多少小时
		long min = diff % nd % nh / nm;
		long sec = diff % nd % nh % nm / ns;
		
		if (day > 0) {
			result += day + "天";
			
		} else {
			if (hour > 0) {
				result += hour + "小时";
			}

			if (min > 0) {
				result += min + "分";
			}

			if (sec > 0) {
				result += sec + "秒";
			}
		}

		
		return result;
	}
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm"); 
	public static String transformStatTime(String createTime) { // 201312081804
		try {
			return sdf.parse(createTime).getTime() + "";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}
	private static SimpleDateFormat hourSdf = new SimpleDateFormat("yyyyMMddHH"); 
	public static String transformStatTimeByHour(String createTime) { // 2013120818
		try {
			return hourSdf.parse(createTime).getTime() + "";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String formatStatTimeByDay(String createTime) { // 201312081804
		return DateUtil.friendlyDate(new Date(Long.parseLong(transformStatTime(createTime))));
	}
	
	public static String formatStatTimeByHour(String createTime) { // 201312081804
		return DateUtil.format(new Date(Long.parseLong(transformStatTime(createTime))), new SimpleDateFormat("yyyy-MM-dd HH:00"));
	}
	
	public static String getItemFpByItemId(Item item) {
		if (item == null) return "";
		
		int type = item.getType();
		switch (type) {
		case Item.TYPE_HTTP:
			return "/item/usability2.do?itemId=" + item.getId();
		case Item.TYPE_PING:
			return "/pingitem/usability2.do?itemId=" + item.getId();
		case Item.TYPE_NGINX:
			return "/nginxitem/summary.do?itemId=" + item.getId();
		case Item.TYPE_REDIS:
			return "/redisitem/summary.do?itemId=" + item.getId();
		}
		return "/item/usability2.do?itemId=" + item.getId();
	}
	
}
