package com.chengyi.eagleeye.network.redis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;
import net.sf.json.JSONObject;

import com.chengyi.eagleeye.model.assist.RedisOption;
import com.chengyi.eagleeye.network.http.HttpClientUtil;
import com.chengyi.eagleeye.network.http.HttpResult;
import com.chengyi.eagleeye.util.CommonUtil;

public class RedisUtil {

	public static RedisResult getRedisStatusPage(String url, RedisOption ro, boolean useSecure) {
		if (useSecure) {
			HttpResult httpResult = HttpClientUtil.get(url);
			if (httpResult == null || httpResult.getStatus() != HttpResult.STATUS_OK) {
				return null;
			} else {
				String responseContent = httpResult.getResponseContent();
				
				responseContent = responseContent.replaceAll("﻿(.*)\\{", "\\{");
				responseContent = responseContent.trim();
				
				RedisResult redisResult = new RedisResult();

				redisResult.setStatus(RedisResult.STATUS_OK);
				redisResult.setResponseContent(responseContent);

				JSONObject result = JSONObject.fromObject(responseContent);
				redisResult.setVersion(result.getString("redis_version"));
				redisResult.setMode(result.getString("redis_mode"));
				redisResult.setTotalTime(result.getLong("uptime_in_days"));
				
				redisResult.setMaxUsedMemory(result.getLong("used_memory_peak"));
				redisResult.setCurrentUsedMemory(result.getLong("used_memory"));
				redisResult.setCurrentCPS(result.getInt("instantaneous_ops_per_sec"));
				
				redisResult.setHitCount(result.getLong("keyspace_hits"));
				redisResult.setMissCount(result.getLong("keyspace_misses"));
				redisResult.setCurrentClients(result.getInt("connected_clients"));
				
				return redisResult;
			}

		} else { // call info
			Jedis jedis = new Jedis(ro.getRedisServerIp(), Integer.parseInt(ro.getRedisServerPort()));
			try {
				String info = jedis.info();
				
				RedisResult redisResult = new RedisResult();

				redisResult.setStatus(RedisResult.STATUS_OK);
				redisResult.setResponseContent(info);
				
				ByteArrayInputStream bais = new ByteArrayInputStream(info.getBytes());
				Properties props = new Properties();
				try {
					props.load(bais);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						bais.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					jedis.disconnect();
				}
				
				redisResult.setVersion(props.getProperty("redis_version"));
				redisResult.setMode(props.getProperty("redis_mode"));
				redisResult.setTotalTime(CommonUtil.getLong(props.getProperty("uptime_in_days")));
				
				redisResult.setMaxUsedMemory(CommonUtil.getLong(props.getProperty("used_memory_peak")));
				redisResult.setCurrentUsedMemory(CommonUtil.getLong(props.getProperty("used_memory")));
				redisResult.setCurrentCPS(CommonUtil.getInteger(props.getProperty("instantaneous_ops_per_sec")));
				
				redisResult.setHitCount(CommonUtil.getLong(props.getProperty("keyspace_hits")));
				redisResult.setMissCount(CommonUtil.getLong(props.getProperty("keyspace_misses")));
				redisResult.setCurrentClients(CommonUtil.getInteger(props.getProperty("connected_clients")));
				
				return redisResult;
			} catch (Exception e) {
				e.printStackTrace();
				RedisResult redisResult = new RedisResult();
				redisResult.setStatus(RedisResult.STATUS_FAIL);
				
				return redisResult;
			}
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
