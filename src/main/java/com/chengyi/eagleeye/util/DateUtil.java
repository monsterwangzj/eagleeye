/*
 * Created on Jan 4, 2008
 */
package com.chengyi.eagleeye.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

/**
 * @author wangzhaojun
 */
public abstract class DateUtil {
	public static final long millisInDay = 86400000;

	private static final SimpleDateFormat mFormatGMT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzz", Locale.US);

	// some static date formats
	private static SimpleDateFormat[] mDateFormats = loadDateFormats();

	private static final SimpleDateFormat mFormat8chars = new SimpleDateFormat("yyyyMMdd");

	private static final SimpleDateFormat mFormat4chars = new SimpleDateFormat("MMdd");
	
	private static final SimpleDateFormat mFormat6chars = new SimpleDateFormat("yy.MMdd");
	
	private static final SimpleDateFormat mFormatIso8601Day = new SimpleDateFormat("yyyy-MM-dd");

	private static final SimpleDateFormat mFormatOnlyTime = new SimpleDateFormat("HH:mm");

	private static final SimpleDateFormat mFormatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private static final SimpleDateFormat mFormatIso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	// http://www.w3.org/Protocols/rfc822/Overview.html#z28
	// Using Locale.US to fix ROL-725 and ROL-628
	private static final SimpleDateFormat mFormatRfc822 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z",
			Locale.US);

	private static final SimpleDateFormat mFormatArchive = new SimpleDateFormat("yyyy-M");

	private static SimpleDateFormat[] loadDateFormats() {
		SimpleDateFormat[] temp = {
		//new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS a"),
				new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy"), // standard Date.toString() results
				new SimpleDateFormat("M/d/yy hh:mm:ss"), new SimpleDateFormat("M/d/yyyy hh:mm:ss"),
				new SimpleDateFormat("M/d/yy hh:mm a"), new SimpleDateFormat("M/d/yyyy hh:mm a"),
				new SimpleDateFormat("M/d/yy HH:mm"), new SimpleDateFormat("M/d/yyyy HH:mm"),
				new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"),
				new SimpleDateFormat("yy-MM-dd HH:mm:ss.SSS"),
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"), // standard Timestamp.toString() results
				new SimpleDateFormat("M-d-yy HH:mm"), new SimpleDateFormat("M-d-yyyy HH:mm"),
				new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS"), new SimpleDateFormat("M/d/yy"),
				new SimpleDateFormat("M/d/yyyy"), new SimpleDateFormat("M-d-yy"),
				new SimpleDateFormat("M-d-yyyy"), new SimpleDateFormat("MMMM d, yyyyy"),
				new SimpleDateFormat("MMM d, yyyyy") };

		mFormatGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
		return temp;
	}

	//-----------------------------------------------------------------------
	/**
	 * Gets the array of SimpleDateFormats that DateUtil knows about.
	 **/
	private static SimpleDateFormat[] getFormats() {
		return mDateFormats;
	}

	//-----------------------------------------------------------------------
	/**
	 * Returns a Date set to the last possible millisecond of the day, just
	 * before midnight. If a null day is passed in, a new Date is created.
	 * midnight (00m 00h 00s)
	 */
	public static Date getEndOfDay(Date day) {
		return getEndOfDay(day, Calendar.getInstance());
	}

	public static Date getEndOfDay(Date day, Calendar cal) {
		if (day == null)
			day = new Date();
		cal.setTime(day);
		cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
		return cal.getTime();
	}

	//-----------------------------------------------------------------------
	/**
	 * Returns a Date set to the first possible millisecond of the day, just
	 * after midnight. If a null day is passed in, a new Date is created.
	 * midnight (00m 00h 00s)
	 */
	public static Date getStartOfDay(Date day) {
		return getStartOfDay(day, Calendar.getInstance());
	}

	/**
	 * Returns a Date set to the first possible millisecond of the day, just
	 * after midnight. If a null day is passed in, a new Date is created.
	 * midnight (00m 00h 00s)
	 */
	public static Date getStartOfDay(Date day, Calendar cal) {
		if (day == null)
			day = new Date();
		cal.setTime(day);
		cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
		return cal.getTime();
	}

	/**
	 * Returns a Date set just to Noon, to the closest possible millisecond
	 * of the day. If a null day is passed in, a new Date is created.
	 * nnoon (00m 12h 00s)
	 */
	public static Date getNoonOfDay(Date day, Calendar cal) {
		if (day == null)
			day = new Date();
		cal.setTime(day);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
		return cal.getTime();
	}

	//-----------------------------------------------------------------------
	public static Date parseFromFormats(String aValue) {
		if (StringUtils.isEmpty(aValue))
			return null;

		// get DateUtil's formats
		SimpleDateFormat formats[] = DateUtil.getFormats();
		if (formats == null)
			return null;

		// iterate over the array and parse
		Date myDate = null;
		for (int i = 0; i < formats.length; i++) {
			try {
				myDate = DateUtil.parse(aValue, formats[i]);
				//if (myDate instanceof Date) 
				return myDate;
			} catch (Exception e) {
				// do nothing because we want to try the next
				// format if current one fails
			}
		}
		// haven't returned so couldn't parse
		return null;
	}

	//-----------------------------------------------------------------------
	public static java.sql.Timestamp parseTimestampFromFormats(String aValue) {
		if (StringUtils.isEmpty(aValue))
			return null;

		// call the regular Date formatter
		Date myDate = DateUtil.parseFromFormats(aValue);
		if (myDate != null)
			return new java.sql.Timestamp(myDate.getTime());
		return null;
	}

	//-----------------------------------------------------------------------
	/**
	 * Returns a java.sql.Timestamp equal to the current time
	 **/
	public static java.sql.Timestamp now() {
		return new java.sql.Timestamp(new java.util.Date().getTime());
	}

	//-----------------------------------------------------------------------
	/**
	 * Returns a string the represents the passed-in date parsed
	 * according to the passed-in format.  Returns an empty string
	 * if the date or the format is null.
	 **/
	public static String format(Date aDate, SimpleDateFormat aFormat) {
		if (aDate == null || aFormat == null) {
			return "";
		}
		synchronized (aFormat) {
			return aFormat.format(aDate);
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Tries to take the passed-in String and format it as a date string in the
	 * the passed-in format.
	 **/
	public static String formatDateString(String aString, SimpleDateFormat aFormat) {
		if (StringUtils.isEmpty(aString) || aFormat == null)
			return "";
		try {
			java.sql.Timestamp aDate = parseTimestampFromFormats(aString);
			if (aDate != null) {
				return DateUtil.format(aDate, aFormat);
			}
		} catch (Exception e) {
			// Could not parse aString.
		}
		return "";
	}

	//-----------------------------------------------------------------------
	/**
	 * Returns a Date using the passed-in string and format.  Returns null if the string
	 * is null or empty or if the format is null.  The string must match the format.
	 **/
	public static Date parse(String aValue, SimpleDateFormat aFormat) throws ParseException {
		if (StringUtils.isEmpty(aValue) || aFormat == null) {
			return null;
		}

		return aFormat.parse(aValue);
	}

	public static Date parseFromArchiveFormat(String value) throws ParseException {
		return parse(value, mFormatArchive);
	}
	
	public static Date parseFromGMTFormat(String value) throws ParseException {
		return parse(value, mFormatGMT);
	}

	public static String formateArchiveDate(Date date, String value) throws ParseException {
		return DateUtil.format(date, mFormatArchive);
	}

	//-----------------------------------------------------------------------
	/**
	 * Returns true if endDate is after startDate or if startDate equals endDate
	 * or if they are the same date.  Returns false if either value is null.
	 **/
	public static boolean isValidDateRange(Date startDate, Date endDate) {
		return isValidDateRange(startDate, endDate, true);
	}

	//-----------------------------------------------------------------------
	/**
	 * Returns true if endDate is after startDate or if startDate equals endDate.
	 * Returns false if either value is null.  If equalOK, returns true if the
	 * dates are equal.
	 **/
	public static boolean isValidDateRange(Date startDate, Date endDate, boolean equalOK) {
		// false if either value is null
		if (startDate == null || endDate == null) {
			return false;
		}

		if (equalOK) {
			// true if they are equal
			if (startDate.equals(endDate)) {
				return true;
			}
		}

		// true if endDate after startDate
		if (endDate.after(startDate)) {
			return true;
		}

		return false;
	}

	//-----------------------------------------------------------------------
	// returns full timestamp format
	public static java.text.SimpleDateFormat defaultTimestampFormat() {
		return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	}

	//-----------------------------------------------------------------------
	// convenience method returns minimal date format
	public static java.text.SimpleDateFormat get8charDateFormat() {
		return DateUtil.mFormat8chars;
	}

	//-----------------------------------------------------------------------
	// convenience method returns minimal date format
	public static java.text.SimpleDateFormat get4charDateFormat() {
		return DateUtil.mFormat4chars;
	}
	
	public static java.text.SimpleDateFormat get6charDateFormat() {
		return DateUtil.mFormat6chars;
	}
	
	//-----------------------------------------------------------------------
	// convenience method returns minimal date format
	public static java.text.SimpleDateFormat defaultDateFormat() {
		return DateUtil.friendlyDateFormat(true);
	}

	//-----------------------------------------------------------------------
	// convenience method
	public static String defaultTimestamp(Date date) {
		return DateUtil.format(date, DateUtil.defaultTimestampFormat());
	}

	//-----------------------------------------------------------------------
	// convenience method
	public static String defaultDate(Date date) {
		return DateUtil.format(date, DateUtil.defaultDateFormat());
	}

	//-----------------------------------------------------------------------
	// convenience method returns long friendly timestamp format
	public static java.text.SimpleDateFormat friendlyTimestampFormat() {
		return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	//-----------------------------------------------------------------------
	// convenience method returns long friendly formatted timestamp
	public static String friendlyTimestamp(Date date) {
		return DateUtil.format(date, DateUtil.friendlyTimestampFormat());
	}
	public static String friendlyTimestamp(long timemillis) {
		return DateUtil.format(new Date(timemillis), DateUtil.friendlyTimestampFormat());
	}
	
	//-----------------------------------------------------------------------
	// convenience method returns long friendly formatted timestamp
	public static String format8chars(Date date) {
		return DateUtil.format(date, mFormat8chars);
	}
	
	//-----------------------------------------------------------------------
	// convenience method returns long friendly formatted timestamp
	public static String format4chars(Date date) {
		return DateUtil.format(date, mFormat4chars);
	}
	
	public static String format6chars(Date date) {
		return DateUtil.format(date, mFormat6chars);
	}

	//-----------------------------------------------------------------------
	// convenience method returns long friendly formatted timestamp
	public static String formatIso8601Day(Date date) {
		return DateUtil.format(date, mFormatIso8601Day);
	}

	//-----------------------------------------------------------------------
	public static String formatOnlyTime(Date date) {
		return DateUtil.format(date, mFormatOnlyTime);
	}

	//-----------------------------------------------------------------------
	public static String formatDateTime(Date date) {
		return DateUtil.format(date, mFormatDateTime);
	}

	//-----------------------------------------------------------------------
	public static String formatRfc822(Date date) {
		return DateUtil.format(date, mFormatRfc822);
	}
	
	//-----------------------------------------------------------------------
	public static String formatGMT(Date date) {
		return DateUtil.format(date, mFormatGMT);
	}

	//-----------------------------------------------------------------------
	// This is a hack, but it seems to work
	public static String formatIso8601(Date date) {
		if (date == null)
			return "";

		// Add a colon 2 chars before the end of the string
		// to make it a valid ISO-8601 date.

		String str = DateUtil.format(date, mFormatIso8601);
		StringBuffer sb = new StringBuffer();
		sb.append(str.substring(0, str.length() - 2));
		sb.append(":");
		sb.append(str.substring(str.length() - 2));
		return sb.toString();
	}

	//-----------------------------------------------------------------------
	// convenience method returns minimal date format
	public static java.text.SimpleDateFormat minimalDateFormat() {
		return DateUtil.friendlyDateFormat(true);
	}

	//-----------------------------------------------------------------------
	// convenience method using minimal date format
	public static String minimalDate(Date date) {
		return DateUtil.format(date, DateUtil.minimalDateFormat());
	}

	//-----------------------------------------------------------------------
	// convenience method that returns friendly data format
	// using full month, day, year digits.
	public static java.text.SimpleDateFormat fullDateFormat() {
		return DateUtil.friendlyDateFormat(false);
	}

	//-----------------------------------------------------------------------
	public static String fullDate(Date date) {
		return DateUtil.format(date, DateUtil.fullDateFormat());
	}

	//-----------------------------------------------------------------------
	/** Returns a "friendly" date format.
	 *  @param mimimalFormat Should the date format allow single digits.
	 **/
	public static java.text.SimpleDateFormat friendlyDateFormat(boolean minimalFormat) {
		if (minimalFormat) {
			return new java.text.SimpleDateFormat("yyyy-M-d");
		}

		return new java.text.SimpleDateFormat("yyyy-MM-dd");
	}

	//-----------------------------------------------------------------------
	/**
	 * Format the date using the "friendly" date format.
	 */
	public static String friendlyDate(Date date, boolean minimalFormat) {
		return DateUtil.format(date, DateUtil.friendlyDateFormat(minimalFormat));
	}

	//-----------------------------------------------------------------------
	// convenience method
	public static String friendlyDate(Date date) {
		return DateUtil.format(date, DateUtil.friendlyDateFormat(true));
	}

	public static String videoTimeint2String(int seconds) {
		StringBuffer result = new StringBuffer("");
		if (seconds > 3600) { // hour:min:sec
			int hour = seconds / 3600;
			int min = (seconds - hour * 3600) / 60;
			int sec = seconds - hour * 3600 - min * 60;

			if (hour < 10) {
				result.append("0").append(hour);
			} else {
				result.append(hour);
			}
			result.append(":");

			if (min < 10) {
				result.append("0").append(min);
			} else {
				result.append(min);
			}
			result.append(":");

			if (sec < 10) {
				result.append("0").append(sec);
			} else {
				result.append(sec);
			}

		} else if (seconds >= 0) {
			int min = seconds / 60;
			int sec = seconds - min * 60;

			if (min < 10) {
				result.append("0").append(min);
			} else {
				result.append(min);
			}
			result.append(":");
			if (sec < 10) {
				result.append("0").append(sec);
			} else {
				result.append(sec);
			}
		} else {
			result = new StringBuffer("-1");
		}
		return result.toString();
	}
	
	public static long getLongByIso8601String(String str) {
		long time = 0L;
		try {
			time = mFormatIso8601Day.parse(str).getTime();
		} catch (ParseException pe) {
		}
		return time;
	}
	
	public static String getIso8601StringByLong(long time) {
		return mFormatIso8601Day.format(new Date(time));
	}
	
	public static int getAge(long time) {
		GregorianCalendar cal = new GregorianCalendar();
		GregorianCalendar calNow = new GregorianCalendar();
		cal.setTimeInMillis(time);
		calNow.setTimeInMillis(System.currentTimeMillis());
		int year = cal.get(Calendar.YEAR);
		int yearNow = calNow.get(Calendar.YEAR);
		return yearNow - year;
	}
	
	public static String getFriendlyTimeDiff(long time, long timeNow) {
		long diff = timeNow - time;
		String str = "";
		if (diff > 0) {
			long s = diff / (60 * 1000);
			long h = s / 60;
			long d = h / 24;
			long m = d / 30;
			long y = m / 12;
			if (y > 0) {
				str = y + "年";
			} else if (m > 0) {
				str = m + "月";
			} else if (d > 0) {
				str = d + "天";
			} else if (h > 0) {
				str = h + "小时";
			} else if (s > 0) {
				str = s + "秒";
			} else {
				str = "毫秒";
			}
		}
		return str;
	}
	
	public static void main(String[] args) {

		Date now = new Date();
		System.out.println("defaultDate: " + DateUtil.defaultDate(now));
		System.out.println("defaultTimestamp: " + DateUtil.defaultTimestamp(now));
		System.out.println("format8chars: " + DateUtil.format8chars(now));
		System.out.println("formatIso8601: " + DateUtil.formatIso8601(now));
		System.out.println("formatIso8601Day: " + DateUtil.formatIso8601Day(now));
		System.out.println("formatRfc822: " + DateUtil.formatRfc822(now));
		System.out.println("friendlyDate: " + DateUtil.friendlyDate(now));
		System.out.println("friendlyTimestamp: " + DateUtil.friendlyTimestamp(now));
		System.out.println("formatGMT: " + DateUtil.formatGMT(now));
		System.out.println("fullDate: " + DateUtil.fullDate(now));
		System.out.println("minimalDate: " + DateUtil.minimalDate(now));

	}
	
	
	
}
