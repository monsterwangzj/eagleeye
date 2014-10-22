package com.chengyi.eagleeye.network.redis;

import java.io.Serializable;

import com.chengyi.eagleeye.network.BaseResult;
import com.chengyi.eagleeye.network.http.HttpResult;

public class RedisResult extends BaseResult implements Serializable {
	private static final long serialVersionUID = -5271815152727671136L;

	public static final int STATUS_OK = 0;
	public static final int STATUS_FAIL = -2001;

	private String version; // 版本号
	private String mode; // redis mode
	private long totalTime; // 运行时间
	private float maxUsedMemory; // 最大使用内存
	private float currentUsedMemory; // 当前使用内存

	private float currentCPS; // 当前执行命令数/秒
	private long hitCount;
	private long missCount;
	private int currentClients; // connected clients

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public float getMaxUsedMemory() {
		return maxUsedMemory;
	}

	public void setMaxUsedMemory(float maxUsedMemory) {
		this.maxUsedMemory = maxUsedMemory;
	}

	public float getCurrentUsedMemory() {
		return currentUsedMemory;
	}

	public void setCurrentUsedMemory(float currentUsedMemory) {
		this.currentUsedMemory = currentUsedMemory;
	}

	public float getCurrentCPS() {
		return currentCPS;
	}

	public void setCurrentCPS(float currentCPS) {
		this.currentCPS = currentCPS;
	}

	public long getHitCount() {
		return hitCount;
	}

	public void setHitCount(long hitCount) {
		this.hitCount = hitCount;
	}

	public long getMissCount() {
		return missCount;
	}

	public void setMissCount(long missCount) {
		this.missCount = missCount;
	}

	public int getCurrentClients() {
		return currentClients;
	}

	public void setCurrentClients(int currentClients) {
		this.currentClients = currentClients;
	}

	
	public int getErrorTypeByResult(BaseResult baseResult) {
		RedisResult redisResult = (RedisResult) baseResult;
		return redisResult.getStatus();
	}

	
	public String getContentByErrorNo(int errorType) {
		String errorReason = "";
		switch (errorType) {
		case RedisResult.STATUS_OK:
			errorReason = "Recover";
			break;
		case RedisResult.STATUS_FAIL:
			errorReason = "Dest Failed";
			break;
		}

		if (errorType > 0) {
			errorReason = "Timeout in " + errorType + " ms";
		}
		return errorReason;
	}

}
