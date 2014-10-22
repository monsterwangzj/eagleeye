/**
 * 2013-7-1 22:41:59
 */
package com.chengyi.eagleeye.network.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.chengyi.eagleeye.network.http.HttpParam.HttpRequestMethod;
import com.chengyi.eagleeye.util.ApplicaRuntime;


/**
 * Http utility Class, support request method of GET, HEAD, POST.
 * 
 * @author wangzhaojun
 * 
 */
public class HttpClientUtil {
	private static final Log logger = LogFactory.getLog(HttpClientUtil.class);

	public static HttpResult get(String uri) {
		HttpParam httpParam = new HttpParam();
		httpParam.setUri(uri);
		httpParam.setMethod(HttpRequestMethod.GET);

		return getHttpResult(httpParam);
	}

	public static HttpResult head(String uri) {
		HttpParam httpParam = new HttpParam();
		httpParam.setUri(uri);
		httpParam.setMethod(HttpRequestMethod.HEAD);

		return getHttpResult(httpParam);
	}

	public static HttpResult post(String uri, Map<String, String> postParams) {
		HttpParam httpParam = new HttpParam();
		httpParam.setUri(uri);
		httpParam.setMethod(HttpRequestMethod.POST);
		httpParam.setPostParams(postParams);

		return getHttpResult(httpParam);
	}

	@SuppressWarnings("unchecked")
	public static HttpResult getHttpResult(HttpParam httpParam) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		int httpTimeout = httpParam.getTimeout();
		if (httpTimeout > 0) {
			httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, httpTimeout);
			httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, httpTimeout);
		}

		HttpRequestBase request = null;
		HttpRequestMethod requestMethod = httpParam.getMethod();

		String uri = httpParam.getUri().trim();
		String host = httpParam.getUri().split("/")[2];
		if (httpParam.getBindAddress() != null) {
			uri = uri.replaceFirst(host, httpParam.getBindAddress());
		}
		
		if (requestMethod == null || requestMethod.equals("") || requestMethod.equals(HttpRequestMethod.GET)) {
			request = new HttpGet(ApplicaRuntime.encodeURI2(uri));
		} else if (requestMethod.equals(HttpRequestMethod.POST)) {
			request = new HttpPost(ApplicaRuntime.encodeURI2(uri));
		} else if (requestMethod.equals(HttpRequestMethod.HEAD)) {
			request = new HttpHead(ApplicaRuntime.encodeURI2(uri));
		}

		if (httpParam.getBindAddress() != null) {
			request.addHeader("Host", host);
		}

		
		// process header
		JSONObject jsonObj = JSONObject.fromObject(httpParam.getJsonHeader());
		Iterator<String> headIt = jsonObj.keySet().iterator();
		while (headIt.hasNext()) {
			String key = headIt.next();
			String value = jsonObj.getString(key);
			request.addHeader(key, value);
		}
		request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64-k6; rv:24.0) Gecko/20100101 Firefox/24.0");
		

		// process cookie
		CookieStore cookieStore = httpclient.getCookieStore();
		String cookiestr = httpParam.getCookie();
		if (cookiestr != null && !cookiestr.equals("")) {
			String[] carr = cookiestr.split(";");
			if (carr != null && carr.length > 0) {
				for (String ele : carr) {
					ele = ele.trim();
					String[] earr = ele.split("=");
					Cookie cookie = new BasicClientCookie(earr[0], earr[1]);
					cookieStore.addCookie(cookie);
				}
			}
		}

		
		// process postParams
		if (requestMethod != null && requestMethod.equals(HttpRequestMethod.POST)) {
			HttpPost postRequest = (HttpPost) request;
			if (httpParam.getPostParams() != null) {
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> entry : httpParam.getPostParams().entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				try {
					postRequest.setEntity(new UrlEncodedFormEntity(nvps, httpParam.getEncoding()));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			return getHttpResult(httpclient, postRequest, httpParam);
		} else {
			return getHttpResult(httpclient, request, httpParam);
		}
	}

	private static HttpResult getHttpResult(DefaultHttpClient httpclient, HttpRequestBase request, HttpParam httpParam) {
		long startTime = System.currentTimeMillis();
		HttpResult httpResult = null;
		String uri = httpParam.getUri();
		try {
			HttpResponse response = httpclient.execute(request);
			
			int status = response.getStatusLine().getStatusCode();
			String resContent = null;
			if (response.getEntity() != null) {
				resContent = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
			
			boolean isMatch = true;
			String resultMatchPattern = httpParam.getResultMatchPattern();
			if ((status == 200 || status == 301 || status == 302) && StringUtils.isNotEmpty(resultMatchPattern)) {
				if (httpParam.getMethod() != null && httpParam.getMethod().equals(HttpRequestMethod.HEAD)) {
					isMatch = true;
				} else if (StringUtils.isEmpty(resContent)) {
					isMatch = false;
				} else {
					isMatch = false;
					
					try {
						Pattern pattern = Pattern.compile(resultMatchPattern);
						Matcher matcher = pattern.matcher(resContent);
						if (httpParam.getResultMatchPatternStatus() == (byte) 1) {
							isMatch = matcher.find();
						} else if (httpParam.getResultMatchPatternStatus() == (byte) 0) {
							isMatch = !(matcher.find());
						}
						if (!isMatch) {
							logger.info("contentNotMatch, uri:" + uri + ", " + httpParam.getBindAddress() + ", regex:" + resultMatchPattern);
							if (logger.isDebugEnabled()) {
								logger.debug("contentNotMatch, uri:" + uri + ", " + httpParam.getBindAddress() + ", regex:" + resultMatchPattern + ", response:" + resContent);
							}
						}
					} catch (Exception e) {
						logger.info(e);
					}
				}
			}
			
			if (status == 200 || status == 301 || status == 302) {
				httpResult = new HttpResult(HttpResult.STATUS_OK, resContent, isMatch);
			} else {
				httpResult = new HttpResult(HttpResult.STATUS_CODE_ABNORMAL, resContent, isMatch);
			}
		} catch (SocketTimeoutException se) {
			Long endTime = System.currentTimeMillis();
			logger.error(uri + " SocketTimeoutException :: time elapsed :: " + (endTime - startTime) + ", ERROR:" + se);
			se.printStackTrace();
			
			httpResult = new HttpResult(httpParam.getTimeout(), se.toString());
		} catch (ConnectTimeoutException cte) {
			Long endTime = System.currentTimeMillis();
			logger.error(uri + " ConnectTimeoutException :: time elapsed :: " + (endTime - startTime) + ", ERROR:" + cte);
			cte.printStackTrace();
			
			httpResult = new HttpResult(httpParam.getTimeout(), cte.toString());
		} catch (HttpHostConnectException cte) {
			Long endTime = System.currentTimeMillis();
			logger.error(uri + " ConnectTimeoutException :: time elapsed :: " + (endTime - startTime) + ", ERROR:" + cte);
			cte.printStackTrace();
			
			httpResult = new HttpResult(HttpResult.STATUS_HTTPABORTED, cte.toString());
		} catch (IOException e) {
			Long endTime = System.currentTimeMillis();
			e.printStackTrace();
			
			logger.error(uri + " ConnectTimeoutException :: time elapsed :: " + (endTime - startTime) + ", ERROR:" + e);
			httpResult = new HttpResult(HttpResult.STATUS_NETWORKINTERRUPT, e.toString());
		} finally {
			request.releaseConnection();
			httpclient.getConnectionManager().shutdown();
		}
		long endTime = System.currentTimeMillis();
		long costTime = endTime - startTime;
		if (httpResult != null)
			httpResult.setCostTime(costTime);
		
		if (httpResult.getStatus() != HttpResult.STATUS_OK) {
			logger.info(uri + ", " + httpResult);
		}
		
		return httpResult;
	}

}
