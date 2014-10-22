package com.chengyi.eagleeye.network.nginx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chengyi.eagleeye.network.http.HttpClientUtil;
import com.chengyi.eagleeye.network.http.HttpResult;
import com.chengyi.eagleeye.util.CommonUtil;

public class NginxUtil {

	public static NginxResult getNginxStatusPage(String url) {
		HttpResult httpResult = HttpClientUtil.get(url);
		if (httpResult == null || httpResult.getStatus() != HttpResult.STATUS_OK) {
			NginxResult nginxResult = new NginxResult();
			nginxResult.setStatus(NginxResult.STATUS_FAIL);
			nginxResult.setResponseContent(httpResult == null ? "" : httpResult.getResponseContent());
			
			return nginxResult;
		} else {
			String responseContent = httpResult.getResponseContent();

			NginxResult nginxResult = new NginxResult();

			nginxResult.setStatus(NginxResult.STATUS_OK);
			nginxResult.setResponseContent(responseContent);

			String activeConn = getMatcher("Active connections: ([\\d]+)", responseContent);
			String readingConn = getMatcher("Reading: ([\\d]+) ", responseContent);
			String writingConn = getMatcher("Writing: ([\\d]+) ", responseContent);
			String waitingConn = getMatcher("Waiting: ([\\d]+) ", responseContent);
			String totalConn = getMatcher(" ([\\d]+) [\\d]+ [\\d]+", responseContent);
			String totalHand = getMatcher(" [\\d]+ ([\\d]+) [\\d]+", responseContent);
			String totalReq = getMatcher(" [\\d]+ [\\d]+ ([\\d]+)", responseContent);

			nginxResult.setActiveConn(CommonUtil.getInteger(activeConn));
			nginxResult.setReadingConn(CommonUtil.getInteger(readingConn));
			nginxResult.setWritingConn(CommonUtil.getInteger(writingConn));
			nginxResult.setWaitingConn(CommonUtil.getInteger(waitingConn));

			nginxResult.setTotalConn(CommonUtil.getInteger(totalConn));
			nginxResult.setTotalHandshake(CommonUtil.getInteger(totalHand));
			nginxResult.setTotalRequest(CommonUtil.getInteger(totalReq));

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
