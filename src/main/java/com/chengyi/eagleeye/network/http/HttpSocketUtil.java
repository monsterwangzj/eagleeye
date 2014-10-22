/**
 * 2013-8-29 22:41:59
 */
package com.chengyi.eagleeye.network.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.chengyi.eagleeye.network.http.HttpParam.HttpRequestMethod;
import com.chengyi.eagleeye.util.ApplicaRuntime;

/**
 * Http Socket Utility Class, support request method of GET, HEAD, POST. Transfer-Encoding: chunked
 * 
 * @author wangzhaojun
 * 
 */
public class HttpSocketUtil {
	private static final Log logger = LogFactory.getLog(HttpSocketUtil.class);

	private static final String CRLF = "\r\n";

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
		String uri = httpParam.getUri();
		String[] uriArr = httpParam.getUri().split("/");
		String host = uriArr[2];
		String path = "/";
		if (uriArr.length >= 3) {
			String tmp = uri.substring(uri.indexOf(host) + host.length());
			if (StringUtils.isNotEmpty(tmp)) {
				path = tmp;
			}
		}
		int port = 80;
		if (host.contains(":")) {
			port = Integer.parseInt(host.substring(host.indexOf(":") + 1));
			host = host.substring(0, host.indexOf(":"));
		}
		String bindAddress = httpParam.getBindAddress();

		
		// 1, DNS lookup
		long dnsLookupCost = 0L;
		InetAddress addr = null;
		boolean hostInIpFormat = true;
		if (ApplicaRuntime.containsAlphabit(host)) { // domain and no serverIp binded
			hostInIpFormat = false;
			long dnsLookupStart = System.currentTimeMillis();
			try {
				addr = InetAddress.getByName(host);
			} catch (UnknownHostException e) {
				logger.error(uri + ", " + e);
				if (StringUtils.isNotEmpty(bindAddress)) {
					try {
						addr = InetAddress.getByName(bindAddress);
					} catch (UnknownHostException e1) {
						logger.error(uri + ", " + e1);
					}
				}
			}
			long dnsLookupEnd = System.currentTimeMillis();
			dnsLookupCost = dnsLookupEnd - dnsLookupStart;
			
			if (addr == null) { // dns error
				HttpResult httpResult = new HttpResult(HttpResult.STATUS_DNS_ERROR, "dns error");
				return httpResult;
			}
		}
		
		System.out.println(uri + "------------------dns lookup cost:" + dnsLookupCost + "ms");


		// 2, Connection creation
		long connectionCreationCost = 0L;
		long connectingStart = System.currentTimeMillis();
		Socket socket = null;
		boolean connectionError = false;
		try {
			if (hostInIpFormat) {
				socket = new Socket(host, port); // TODO: param config, for example so timeout
			} else {
				socket = new Socket(addr, port);
			}
//			socket.setSoTimeout(5000);
			socket.setSoTimeout(10);
			OutputStream os = socket.getOutputStream();

			// process header TODO: to be checked
			StringBuilder head = new StringBuilder();
			head.append(httpParam.getMethod()).append(" ").append(path).append(" HTTP/1.1").append(CRLF);
			if (StringUtils.isNotEmpty(bindAddress)) {
				head.append("Host:" + addr.getHostAddress() + CRLF);
			} else {
				head.append("Host:" + host + CRLF);
			}
			JSONObject jsonObj = JSONObject.fromObject(httpParam.getJsonHeader());
			Iterator<String> headIt = jsonObj.keySet().iterator();
			while (headIt.hasNext()) {
				String key = headIt.next();
				String value = jsonObj.getString(key);
				// head.append(key + ":" + value + CRLF);
			}
			
			head.append("Cache-Control: max-age=0");
			
			// head.append("User-Agent:Mozilla/4.0" + CRLF);
			// head.append("Accept: text/html,application/xhtml+xml,application/xml" + CRLF);
			// head.append("Accept-Encoding: gzip,deflate,sdch" + CRLF);
			// head.append("Accept-Language: zh-CN,zh;q=0.8" + CRLF);
			head.append("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			head.append("Accept-Encoding: gzip, deflate");
			head.append("Accept-Language: en-US,en;q=0.5");
			head.append("User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64; rv:24.0) Gecko/20100101 Firefox/24.0");
			
			
			
			
			// process cookie TODO: to be checked
			String cookiestr = httpParam.getCookie();
			if (StringUtils.isNotEmpty(cookiestr)) {
				head.append("Cookie: " + cookiestr + CRLF);
				// head.append("Cookie: SESSID=123456abc789" + CRLF);
			}

			// process post params TODO: to be checked
			// head.append("Connection: close\r\n");
			head.append("Connection: keep-alive\r\n");

			head.append(CRLF + CRLF);
//			System.out.println(uri + ", head:" + head);
			os.write(head.toString().getBytes());
			os.flush();

			long connectingEnd = System.currentTimeMillis();
			connectionCreationCost = connectingEnd - connectingStart;
		} catch (Exception e1) {
			logger.error(uri + ", " + e1);
			connectionError = true;
		}
		if (connectionError) {
			HttpResult httpResult = new HttpResult(HttpResult.STATUS_CONNECTION_ERROR, "connection time out");
			return httpResult;
		}
		System.out.println(uri + "------------------connecting cost:" + connectionCreationCost + "ms");

		
		// 3, Waiting response
		long waitingCost = 0L, receivingCost = 0L;
		long waitingStart = System.currentTimeMillis();
		long receivingStart = 0L;
		InputStream is = null;
		int status = -1;
		StringBuffer contentRes = new StringBuffer();
		BodyBytes cbys = new BodyBytes();
		Map<String, String> headerMap = new HashMap<String, String>();
		int headerLength = -1;
		boolean parseHeaderError = false;
		try {
			is = socket.getInputStream();
			cbys.readBytes = new byte[DefaultSize];
			cbys.readLength = is.read(cbys.readBytes);

			// parse header
			int idx = 3;
			while (idx < cbys.readLength && !(cbys.readBytes[idx - 3] == CR && cbys.readBytes[idx - 2] == LF && cbys.readBytes[idx - 1] == CR && cbys.readBytes[idx] == LF)) {
				idx++;
			}
			long waitingEnd = System.currentTimeMillis();
			waitingCost = waitingEnd - waitingStart;
			System.out.println(uri + "------------------waiting cost:" + (waitingCost) + "ms");

			String header = new String(cbys.readBytes, 0, idx + 1);
			String[] headerEle = header.split("\r\n");
			for (String p : headerEle) {
				if (p.contains("HTTP/")) {
					String[] tmpa = p.split("\\s");
					if (tmpa != null && tmpa.length >= 2) {
						status = Integer.parseInt(tmpa[1]);
					}
				}
				String[] tmpb = p.split(":");
				if (tmpb != null && tmpb.length >= 2) {
					headerMap.put(tmpb[0].trim(), tmpb[1].trim());
				}
			}
			headerLength = idx;
			if (headerLength == -1) {
				throw new RuntimeException("http返回的头部信息不标准，没有头部信息！");
			}
		} catch (IOException e1) {
			logger.error(uri + ", " + e1);
			parseHeaderError = true;
		}
		if (parseHeaderError) {
			HttpResult httpResult = new HttpResult(HttpResult.STATUS_PARSEHEADER_ERROR, "parse header error");
			return httpResult;
		}

		
		// 4, receiving response html body
		receivingStart = System.currentTimeMillis();
		cbys.contentStart = headerLength + 1;
		cbys.contentLength = getContentLength(headerMap);
		ByteArrayOutputStream rs = null;
		boolean receiveError = false;
		try {
			if (cbys.contentLength >= 0) {
				System.out.println(uri + "-----getBodyByContextLength");
				rs = getBodyByContextLength(is, cbys);
			} else if (isChunked(headerMap)) {
				System.out.println(uri + "-----getBodyByChunked");
				rs = getBodyByChunked(is, cbys);
			} else {
				throw new RuntimeException(uri + "http返回的头部信息不标准，无法取得正文的长度:Content-Length");
			}
			contentRes = new StringBuffer().append(new String(rs.toByteArray(), "UTF-8"));

			long receivingEnd = System.currentTimeMillis();
			receivingCost = receivingEnd - receivingStart;
			logger.info(uri + "------------------receiving cost:" + (receivingCost) + "ms");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(uri + ", " + e);
			receiveError = true;
		}
		if (receiveError) {
			HttpResult httpResult = new HttpResult(HttpResult.STATUS_RECEIVE_ERROR, "receive error");
			return httpResult;
		}
		
		HttpResult httpResult = new HttpResult(status, contentRes.toString());
		httpResult.setDnsLookupCost(dnsLookupCost);
		httpResult.setConnectionCreationCost(connectionCreationCost);
		httpResult.setWaitingCost(waitingCost);
		httpResult.setReceivingCost(receivingCost);
		httpResult.setCostTime(dnsLookupCost + connectionCreationCost + waitingCost + receivingCost);
		
		return httpResult;
	}

	static int DefaultSize = 4 * 1024;

	private static ByteArrayOutputStream getBodyByContextLength(InputStream is, BodyBytes cbys) throws IOException {
		ByteArrayOutputStream rs = new ByteArrayOutputStream();
		if (cbys.contentLength == 0) {
			return rs;
		}
		int size = cbys.readLength - cbys.contentStart;
		rs.write(cbys.readBytes, cbys.contentStart, size);

		int count = 0;// 计数器
		int resize = cbys.contentLength - size; // 剩余的长度
		byte[] bys = new byte[resize];
		while (count < resize) {
			int l = is.read(bys);
			if (l != -1) {
				count += l;
				rs.write(bys, 0, l);
			}
		}
		return rs;
	}

	/**
	 * 根据chuned读取正文
	 * 
	 * @param is
	 *            socketInputStream Socket输入流
	 * @param cbys
	 * @return
	 * @throws IOException
	 */
	private static ByteArrayOutputStream getBodyByChunked(InputStream is, BodyBytes cbys) throws IOException {
		ByteArrayOutputStream rs = new ByteArrayOutputStream();
		cbys = getChunkedSize(is, cbys);
		// is.read();
		while (cbys.contentLength > 0) {
			// chunkedSize减去已读取的字节
			int readlen = cbys.readLength - cbys.contentStart, textlen = cbys.contentLength - readlen;
			if (textlen > 0) {// 跨越这个byte[]范围
				rs.write(cbys.readBytes, cbys.contentStart, readlen);
				int rlen = 0, count = 0;
				while (rlen < textlen) {
					byte[] bys = new byte[textlen - rlen];// 剩余的字节数(不能多读)
					int l = is.read(bys);
					rs.write(bys, 0, l);
					rlen += l;
					count++;
				}
				// 处理下一段chunked
				BodyBytes cb = new BodyBytes();
				cb.readBytes = new byte[DefaultSize];
				cb.readLength = is.read(cb.readBytes);
				cb.contentStart = 2;// 跳过第一个分隔符
				cb.contentLength = -1;
				cbys = getChunkedSize(is, cb);// 处理下一段chunked
			} else {
				rs.write(cbys.readBytes, cbys.contentStart, cbys.contentLength); // 在这个byte[]范围内
				cbys.contentStart = cbys.contentLength + cbys.contentStart;
				cbys.contentStart += 2;// 跳过第一个分隔符
				cbys = getChunkedSize(is, cbys);// 处理下一段chunked
			}
		}
		return rs;
	}

	private static BodyBytes getChunkedSize(InputStream is, BodyBytes cbys) throws IOException {
		// 在第一个换行前的字符就是ChunedSize

		int contentStart = cbys.contentStart, readLength = cbys.readLength;
		byte[] bys = cbys.readBytes;
		int sizeEnd = getLineEndIndex(bys, contentStart, readLength);

		// 定义chunked的大小字节位置，如果找不到则继续读取字节流
		while (sizeEnd == -1) {
			byte[] b = new byte[DefaultSize];
			int l = is.read(b);
			if (l > 0) {
				byte[] b2 = new byte[readLength + l];
				System.arraycopy(bys, 0, b2, 0, readLength);
				System.arraycopy(b, 0, b2, readLength, l);

				bys = b2;
				contentStart = readLength;
				readLength = readLength + l;
				sizeEnd = getLineEndIndex(bys, contentStart, readLength);
			} else {
				throw new RuntimeException("找不到chunked的大小信息");
			}
		}

		// ///////计算chunked有大小////////////////
		String lens = new String(bys, contentStart, sizeEnd - contentStart - 1);
		BodyBytes rs = new BodyBytes();// 回写信息
		try {
			rs.contentLength = Integer.parseInt(lens, 16);
		} catch (NumberFormatException e) {
			throw e;
		}
		rs.readLength = readLength;
		rs.contentStart = sizeEnd + 1;// 有两位是换行符
		rs.readBytes = bys;
		return rs;
	}

	/**
	 * 从start位置开始，找出回车符的结束位置
	 * 
	 * @param bys
	 *            字节
	 * @param start
	 *            开始位置
	 * @param endIdx
	 *            结束位置
	 * @return 结束位置(指向最后的回车符位置\n)
	 */
	private static int getLineEndIndex(byte[] bys, int start, int endIdx) {
		int idx = -1;
		for (int i = start + 1; i < endIdx; i++) {
			if (bys[i - 1] == CR && bys[i] == LF) {
				idx = i;
				break;
			}
		}
		return idx;
	}

	private static boolean isChunked(Map<String, String> headerMap) {
		String rs = headerMap.get("Transfer-Encoding");
		return (rs != null && rs.indexOf("chunked") != -1);
	}

	private static int getContentLength(Map<String, String> headerMap) {
		String rs = headerMap.get("Content-Length");
		int length = -1;
		try {
			length = Integer.parseInt(rs);
		} catch (NumberFormatException e) {
//			e.printStackTrace();
		}
		return length;
	}

	public static final int CR = 13;
	public static final int LF = 10;

	private static class BodyBytes {
		/** 已读取的字节 */
		public byte[] readBytes;
		/** 正文开始的位置（相对主体的偏移），也是头部长度,指向第一个开始字符（非换行回车） */
		public int contentStart;
		/** 已读取字节的长度 */
		public int readLength;
		/** 正文长度 */
		public int contentLength;
		
		public String getText() {
			int readlen = contentLength;
			if (readlen <= 0)
				readlen = readLength - contentStart;
			return getText(contentStart, readlen);
		}

		private String getText(int start, int length) {
			String s = "null";
			try {
				if (readBytes != null) {
					s = new String(readBytes, start, length, "utf-8");
				}
			} catch (UnsupportedEncodingException e) {
			}
			return s;
		}

		
		public String toString() {
			return getText(0, readLength);
		}
	}

}
