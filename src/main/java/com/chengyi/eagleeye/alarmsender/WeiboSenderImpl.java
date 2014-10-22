
package com.chengyi.eagleeye.alarmsender;

/**
 * @author wangzhaojun
 * 
 */
public class WeiboSenderImpl implements EagleSender {

	private WeiboSenderImpl() {
	}

	private static WeiboSenderImpl instance = new WeiboSenderImpl();

	public boolean send(String to, String content) {
		return false;
	}

	public static EagleSender getInstance() {
		return instance;
	}

}
