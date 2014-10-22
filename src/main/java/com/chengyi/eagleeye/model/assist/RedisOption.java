package com.chengyi.eagleeye.model.assist;

import java.io.Serializable;

public class RedisOption implements Serializable {
	private static final long serialVersionUID = -2865873651663276304L;

	private String redisServerIp;
	private String redisServerPort;

	public String getRedisServerIp() {
		return redisServerIp;
	}

	public void setRedisServerIp(String redisServerIp) {
		this.redisServerIp = redisServerIp;
	}

	public String getRedisServerPort() {
		return redisServerPort;
	}

	public void setRedisServerPort(String redisServerPort) {
		this.redisServerPort = redisServerPort;
	}

	public String toString() {
		return redisServerIp + ":" + redisServerPort;
	}

}
