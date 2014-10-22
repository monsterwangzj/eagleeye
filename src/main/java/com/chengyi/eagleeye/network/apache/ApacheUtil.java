package com.chengyi.eagleeye.network.apache;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chengyi.eagleeye.network.http.HttpClientUtil;
import com.chengyi.eagleeye.network.http.HttpResult;

public class ApacheUtil {

	public static ApacheResult getApacheStatusPage(String url) {
		HttpResult httpResult = HttpClientUtil.get(url);
		if (httpResult == null || httpResult.getStatus() != HttpResult.STATUS_OK) {
			return null;
		} else {
			String responseContent = httpResult.getResponseContent();

			ApacheResult nginxResult = new ApacheResult();

			nginxResult.setStatus(ApacheResult.STATUS_OK);
			nginxResult.setResponseContent(responseContent);

			 

			return nginxResult;
		}

	}

	public static String getMatcher(String regex, String source) {
		String result = "";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		while (matcher.find()) {
			result = matcher.group(1); // 只取第一组
		}
		return result;
	}

}
