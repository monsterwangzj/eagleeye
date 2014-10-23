package com.chengyi.eagleeye.patrol;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.chengyi.eagleeye.model.message.Message;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.SerializableUtil;

public class RedisUtil {
	private static Logger logger = Logger.getLogger(RedisUtil.class);

	public static int expireSeconds = 60 * 24 * 3600;

	public static ShardedJedisPool shardedJedisPool = null;
	static {
		List<JedisShardInfo> jedisShardInfos = new ArrayList<JedisShardInfo>();
		jedisShardInfos.add(new JedisShardInfo("stockcommunity.d5795aqcn.alipay.net", 6379, 1000));

		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxActive(60000);
		poolConfig.setMaxIdle(1000);
		poolConfig.setMaxWait(1000L);
		// poolConfig.setTestOnBorrow(true);

		shardedJedisPool = new ShardedJedisPool(poolConfig, jedisShardInfos);
	}

	public static Object update(String key, Object obj) {
		return save(key, obj);
	}

	public static Object save(String key, Object obj) {
		long startTime = System.currentTimeMillis();
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		try {
			shardedJedis.setex(key.getBytes(), expireSeconds, SerializableUtil.serialize(obj));

			long endTime = System.currentTimeMillis();
			if (endTime - startTime > 5) {
				logger.info("key:" + key + ", cost:" + (endTime - startTime) + " ms.");
			}

			shardedJedisPool.returnResource(shardedJedis);
			return obj;
		} catch (Exception e) {
			logger.error(e);
			if (shardedJedis != null) {
				shardedJedisPool.returnBrokenResource(shardedJedis);
			}
		}
		return null;
	}

	public static Long setLong(String key, Long num) {
		long startTime = System.currentTimeMillis();
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		try {
			shardedJedis.setex(key, expireSeconds, num.toString());

			long endTime = System.currentTimeMillis();
			if (endTime - startTime > 5) {
				logger.info("key:" + key + ", cost:" + (endTime - startTime) + " ms.");
			}

			shardedJedisPool.returnResource(shardedJedis);
			return num;
		} catch (Exception e) {
			logger.error(e);
			if (shardedJedis != null) {
				shardedJedisPool.returnBrokenResource(shardedJedis);
			}
		}
		return null;
	}

	public static Long getLong(String key) {
		long startTime = System.currentTimeMillis();

		ShardedJedis jedis = shardedJedisPool.getResource();
		try {
			String num = jedis.get(key);
			Long result = null;
			try {
				result = Long.parseLong(num);
			} catch (Exception e) {
			}

			shardedJedisPool.returnResource(jedis);

			long endTime = System.currentTimeMillis();
			if (endTime - startTime > 5) {
				logger.info("key:" + key + ", cost:" + (endTime - startTime) + " ms.");
			}
			return result;
		} catch (Exception e) {
			logger.error(e);
			if (jedis != null) {
				shardedJedisPool.returnBrokenResource(jedis);
			}
			return -1L;
		}
	}

	public static Object get(String key) {
		long startTime = System.currentTimeMillis();

		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		try {
			Object obj = (Object) SerializableUtil.deSerialize(shardedJedis.get(key.getBytes()));
			shardedJedisPool.returnResource(shardedJedis);

			long endTime = System.currentTimeMillis();
			if (endTime - startTime > 5) {
				logger.info("key:" + key + ", cost:" + (endTime - startTime) + " ms.");
			}
			return obj;
		} catch (Exception e) {
			logger.error(e);
			if (shardedJedis != null) {
				shardedJedisPool.returnBrokenResource(shardedJedis);
			}
		}
		return null;
	}

	public static boolean remove(String key) {
		long startTime = System.currentTimeMillis();
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		try {
			shardedJedis.del(key);
			shardedJedisPool.returnResource(shardedJedis);

			long endTime = System.currentTimeMillis();
			if (endTime - startTime > 5) {
				logger.info("key:" + key + ", cost:" + (endTime - startTime) + " ms.");
			}
			return true;
		} catch (Exception e) {
			logger.error(e);
			if (shardedJedis != null) {
				shardedJedisPool.returnBrokenResource(shardedJedis);
			}
		}
		return false;
	}

	public static Long getCurrentMessageId() {
		final String key = ApplicaRuntime.globalFlag + "CURRENT_MESSAGE_KEY";

		long startTime = System.currentTimeMillis();

		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		try {
			Long id = shardedJedis.incr(key);
			shardedJedisPool.returnResource(shardedJedis);

			long endTime = System.currentTimeMillis();
			if (endTime - startTime > 5) {
				logger.info("key:" + key + ", cost:" + (endTime - startTime) + " ms.");
			}
			return id;
		} catch (Exception e) {
			logger.error(e);
			if (shardedJedis != null) {
				shardedJedisPool.returnBrokenResource(shardedJedis);
			}
		}
		return -1L;
	}
	
	public static Long getCurrentEventSeqId() {
		final String key = ApplicaRuntime.globalFlag + "CURRENT_EVENTSEQ_KEY";

		long startTime = System.currentTimeMillis();

		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		try {
			Long id = shardedJedis.incr(key);
			shardedJedisPool.returnResource(shardedJedis);

			long endTime = System.currentTimeMillis();
			if (endTime - startTime > 5) {
				logger.info("key:" + key + ", cost:" + (endTime - startTime) + " ms.");
			}
			return id;
		} catch (Exception e) {
			logger.error(e);
			if (shardedJedis != null) {
				shardedJedisPool.returnBrokenResource(shardedJedis);
			}
		}
		return -1L;
	}

	public static void pushMessage2PendingSet(Message message) {
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		String setname = ApplicaRuntime.globalFlag + "PS2_message";

		try {
			message.setId(getCurrentMessageId());
			logger.debug("MESSAGE:" + message);
			shardedJedis.lpush(setname.getBytes(), SerializableUtil.serialize(message));

			shardedJedisPool.returnResource(shardedJedis);
		} catch (Exception e) {
			logger.error(e + ", setname:" + setname + ", messageId:" + message.getId());
			if (shardedJedis != null) {
				shardedJedisPool.returnBrokenResource(shardedJedis);
			}
		}
	}

	public static Message popMessageFromPendingSet() {
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		String setname = ApplicaRuntime.globalFlag + "PS2_message";

		try {
			byte[] bytearr = shardedJedis.rpop(setname.getBytes());
			Message message = (Message) SerializableUtil.deSerialize(bytearr);

			shardedJedisPool.returnResource(shardedJedis);

			return message;
		} catch (Exception e) {
			logger.error(e + ", setname:" + setname);
			if (shardedJedis != null) {
				shardedJedisPool.returnBrokenResource(shardedJedis);
			}
		}
		return null;
	}

	public static Long incr(String key) {
		return incr(key, 1L);
	}

	public static Long incr(String key, Long num) {
		long startTime = System.currentTimeMillis();
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		try {
			Long value = shardedJedis.incrBy(key, num);

			long endTime = System.currentTimeMillis();
			if (endTime - startTime > 5) {
				logger.info("key:" + key + ", cost:" + (endTime - startTime) + " ms.");
			}

			shardedJedisPool.returnResource(shardedJedis);
			return value;
		} catch (Exception e) {
			logger.error(e);
			if (shardedJedis != null) {
				shardedJedisPool.returnBrokenResource(shardedJedis);
			}
		}
		return null;
	}

}
