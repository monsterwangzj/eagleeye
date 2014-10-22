package com.chengyi.eagleeye.network.redis;

import java.io.Serializable;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

import com.chengyi.eagleeye.model.assist.RedisOption;
import com.chengyi.eagleeye.network.BaseParam;

public class RedisParam extends BaseParam implements Serializable{
	private static final long serialVersionUID = 7967940752759511510L;
	
	public static Logger logger = Logger.getLogger(RedisParam.class);

	private RedisResult lastErrorRedisResult;
	
	private boolean useSecure = false;
	private RedisOption redisOption = null;
	
	public RedisParam (){}
	
	public RedisParam (Long  itemId, String uri) {
		setItemId(itemId);
		setUri(uri);
		useSecure = true;
	}
	
	public RedisParam(Long  itemId, String uri, String redisOptions) {
		setItemId(itemId);
		if (StringUtils.isNotEmpty(redisOptions)) {
			redisOption = (RedisOption) JSONObject.toBean(JSONObject.fromObject(redisOptions), RedisOption.class);
			useSecure = false;
		} else if (StringUtils.isNotEmpty(uri)) {
			setUri(uri);
			useSecure = true;
		}
		
	}
	
	public void add2Queue(int status) {
		if (queue.size() < 14) {
			queue.add(status);
		} else {
			queue.remove(0);
			queue.add(status);
		}
		logger.info(getUri() + ":" + super.getBindAddress() + ":" + queue);
	}
	
	public int sumQueueChangeCount() {
		int sum = 0;
		for (int i = 1; i < queue.size(); i++) {
			if (!queue.get(i).equals(queue.get(i - 1))) {
				sum++;
			}
		}
		return sum;
	}
	
	public String getWorkUri() {
		if (super.uri != null) {
			return super.uri;
		} else {
			return redisOption.toString();
		}
	}
	
	public RedisResult getLastErrorResult() {
		return this.lastErrorRedisResult;
	}

	public void setLastErrorRedisResult(RedisResult lastErrorRedisResult) {
		this.lastErrorRedisResult = lastErrorRedisResult;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

	public boolean isUseSecure() {
		return useSecure;
	}

	public void setUseSecure(boolean useSecure) {
		this.useSecure = useSecure;
	}

	public RedisOption getRedisOption() {
		return redisOption;
	}

	public void setRedisOption(RedisOption redisOption) {
		this.redisOption = redisOption;
	}
	
}
