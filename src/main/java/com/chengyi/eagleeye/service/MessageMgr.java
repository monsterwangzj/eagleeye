package com.chengyi.eagleeye.service;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.chengyi.eagleeye.model.message.MessageStat;
import com.chengyi.eagleeye.model.message.http.HttpMessage;
import com.chengyi.eagleeye.model.message.http.HttpMessageStat;
import com.chengyi.eagleeye.model.message.nginx.NginxMessage;
import com.chengyi.eagleeye.model.message.nginx.NginxMessageStat;
import com.chengyi.eagleeye.model.message.ping.PingMessage;
import com.chengyi.eagleeye.model.message.ping.PingMessageStat;
import com.chengyi.eagleeye.model.message.redis.RedisMessage;
import com.chengyi.eagleeye.model.message.redis.RedisMessageStat;
import com.chengyi.eagleeye.network.http.HttpResult;
import com.chengyi.eagleeye.network.http.StatHttpMessage;
import com.chengyi.eagleeye.network.nginx.NginxResult;
import com.chengyi.eagleeye.patrol.DaoImpl;
import com.chengyi.eagleeye.util.CommonUtil;
import com.chengyi.eagleeye.util.DateUtil;

@SuppressWarnings("unchecked")
public class MessageMgr {

	@Autowired
	private DaoImpl dao;

	public HttpMessage saveHttpMessage(HttpMessage httpMessage) {
		dao.getHibernateTemplate().save(httpMessage);
		return httpMessage;
	}

	public List<HttpMessage> getHttpMessageByTimeDiff(Long startTime, Long endTime) {
		final String hql = "from HttpMessage where createTime>=" + startTime + " and createTime<" + endTime;

		return dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
	}
	
	public List<PingMessage> getPingMessageByTimeDiff(Long startTime, Long endTime) {
		final String hql = "from PingMessage where createTime>=" + startTime + " and createTime<" + endTime;

		return dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
	}
	
	public List<NginxMessage> getNginxMessageByTimeDiff(Long startTime, Long endTime) {
		final String hql = "from NginxMessage where createTime>=" + startTime + " and createTime<" + endTime;

		return dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
	}
	
	public List<RedisMessage> getRedisMessageByTimeDiff(Long startTime, Long endTime) {
		final String hql = "from RedisMessage where createTime>=" + startTime + " and createTime<" + endTime;

		return dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
	}

	public void deleteHttpMessageByTime(Long endTime) {
		final String hql = "DELETE from HttpMessage where createTime<" + endTime;
		dao.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				int rcd = query.executeUpdate();
				System.out.println("删除的记录个数:" + rcd);
				return rcd;
			}
		});
	}
	
	public void deletePingMessageByTime(Long endTime) {
		final String hql = "DELETE from PingMessage where createTime<" + endTime;
		dao.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				int rcd = query.executeUpdate();
				System.out.println("删除的记录个数:" + rcd);
				return rcd;
			}
		});
	}

	public void deleteNginxMessageByTime(Long endTime) {
		final String hql = "DELETE from NginxMessage where createTime<" + endTime;
		dao.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				int rcd = query.executeUpdate();
				System.out.println("删除的记录个数:" + rcd);
				return rcd;
			}
		});
	}
	
	public void deleteRedisMessageByTime(Long endTime) {
		final String hql = "DELETE from RedisMessage where createTime<" + endTime;
		dao.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				int rcd = query.executeUpdate();
				System.out.println("删除的记录个数:" + rcd);
				return rcd;
			}
		});
	}
	
	/**
	 * 
	 * @param itemId
	 * @param start
	 *            类似2014021310格式
	 * @param end
	 * @return
	 */
	public List<HttpMessageStat> getHttpMessageStatsByDateInterval(Long itemId, Long start, Long end) {
		final String hql = "from HttpMessageStat where itemId=" + itemId + " and createTime>=" + start + " and createTime<" + end + " and (serverIp is null or serverIp='') order by createtime asc";

		return dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
	}

	public List<PingMessageStat> getPingMessageStatsByDateInterval(Long itemId, Long start, Long end) {
		final String hql = "from PingMessageStat where itemId=" + itemId + " and createTime>=" + start + " and createTime<" + end + " and (serverIp is null or serverIp='') order by createtime asc";

		return dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
	}
	
	public List<NginxMessageStat> getNginxMessageStatsByDateInterval(Long itemId, Long start, Long end) {
		final String hql = "from NginxMessageStat where itemId=" + itemId + " and createTime>=" + start + " and createTime<" + end + " and (serverIp is null or serverIp='') order by createtime asc";

		return dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
	}
	
	public List<RedisMessageStat> getRedisMessageStatsByDateInterval(Long itemId, Long start, Long end) {
		final String hql = "from RedisMessageStat where itemId=" + itemId + " and createTime>=" + start + " and createTime<" + end + " and (serverIp is null or serverIp='') order by createtime asc";

		return dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
	}
	
	/**
	 * 
	 * @param itemId
	 * @param startTime
	 *            时间戳格式
	 * @param endTime
	 * @return
	 */
	public HttpMessageStat getHttpMessageStatByDateInterval(Long itemId, Long startTime, Long endTime) {
		final String hql = "from HttpMessage where itemId=" + itemId + " and createTime>=" + startTime + " and createTime<" + endTime + " and (serverIp is null or serverIp='') order by createtime asc";

		List<HttpMessage> hms = dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
		if (CollectionUtils.isNotEmpty(hms)) {
			HttpMessageStat hmStat = new HttpMessageStat();

			for (HttpMessage hm : hms) {
				hmStat.setUserId(hm.getUserId());
				hmStat.setItemId(hm.getItemId());
				hmStat.setServerIp(hm.getServerIp());
				hmStat.setType(hm.getType());
				hmStat.setWorkerIp(hm.getWorkerIp());
				hmStat.setTimeUnitType(MessageStat.TIMEUNITTYPE_HOUR);
				hmStat.setCreateTime(CommonUtil.getLong(DateUtil.format(new Date(startTime), StatHttpMessage.mFormatDateTime)));

				hmStat.setTotalAccessCount(hmStat.getTotalAccessCount() + 1);
				if (hm.getStatus() == HttpResult.STATUS_OK) {
					hmStat.setSuccAccessCount(hmStat.getSuccAccessCount() + 1);
					hmStat.setSuccAccessCostTime(hmStat.getSuccAccessCostTime() + hm.getTotalTime());

					hmStat.setSuccDnsLookupTime(hmStat.getSuccDnsLookupTime() + hm.getDnsLookupTime());
					hmStat.setSuccConnectingTime(hmStat.getSuccConnectingTime() + hm.getConnectingTime());
					hmStat.setSuccWaitingTime(hmStat.getSuccWaitingTime() + hm.getWaitingTime());
					hmStat.setSuccReceivingTime(hmStat.getSuccReceivingTime() + hm.getReceivingTime());

					if (hmStat.getMinResponseTime() == 0L) {
						hmStat.setMinResponseTime(hm.getTotalTime());
					} else {
						if (hmStat.getMinResponseTime() > hm.getTotalTime()) {
							hmStat.setMinResponseTime(hm.getTotalTime());
						}
					}
					if (hmStat.getMaxResponseTime() == 0L) {
						hmStat.setMaxResponseTime(hm.getTotalTime());
					} else {
						if (hmStat.getMaxResponseTime() < hm.getTotalTime()) {
							hmStat.setMaxResponseTime(hm.getTotalTime());
						}
					}

					int classLevel = HttpMessageStat.getClassByResponseTime(hm.getTotalTime());
					switch (classLevel) {
					case 1:
						hmStat.setClass1Count(hmStat.getClass1Count() + 1);
						break;
					case 2:
						hmStat.setClass2Count(hmStat.getClass2Count() + 1);
						break;
					case 3:
						hmStat.setClass3Count(hmStat.getClass3Count() + 1);
						break;
					case 4:
						hmStat.setClass4Count(hmStat.getClass4Count() + 1);
						break;
					case 5:
						hmStat.setClass5Count(hmStat.getClass5Count() + 1);
						break;
					case 6:
						hmStat.setClass6Count(hmStat.getClass6Count() + 1);
						break;
					default:
						hmStat.setClass6Count(hmStat.getClass6Count() + 1);
						break;
					}
				}
			}
			return hmStat;
		}

		return null;
	}
	
	public PingMessageStat getPingMessageStatByDateInterval(Long itemId, Long startTime, Long endTime) {
		final String hql = "from PingMessage where itemId=" + itemId + " and createTime>=" + startTime + " and createTime<" + endTime + " and (serverIp is null or serverIp='') order by createtime asc";

		List<PingMessage> hms = dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
		if (CollectionUtils.isNotEmpty(hms)) {
			PingMessageStat hmStat = new PingMessageStat();

			for (PingMessage hm : hms) {
				hmStat.setUserId(hm.getUserId());
				hmStat.setItemId(hm.getItemId());
				hmStat.setServerIp(hm.getServerIp());
				hmStat.setType(hm.getType());
				hmStat.setWorkerIp(hm.getWorkerIp());
				hmStat.setTimeUnitType(MessageStat.TIMEUNITTYPE_HOUR);
				hmStat.setCreateTime(CommonUtil.getLong(DateUtil.format(new Date(startTime), StatHttpMessage.mFormatDateTime)));

				if (hm.getStatus() == HttpResult.STATUS_OK) {
					if (hmStat.getMinResponseTime() == 0L) {
						hmStat.setMinResponseTime(hm.getMinimum());
					} else {
						if (hmStat.getMinResponseTime() > hm.getMinimum()) {
							hmStat.setMinResponseTime(hm.getMinimum());
						}
					}
					
					if (hmStat.getMaxResponseTime() == 0L) {
						hmStat.setMaxResponseTime(hm.getMaximum());
					} else {
						if (hmStat.getMaxResponseTime() < hm.getMaximum()) {
							hmStat.setMaxResponseTime(hm.getMaximum());
						}
					}
					
					hmStat.setAverageResponseTime((hmStat.getAverageResponseTime() * hmStat.getTotalAccessCount() + hm.getAverage()) / (hmStat.getTotalAccessCount() + 1));

					
				
					int classLevel = PingMessageStat.getClassByResponseTime(hm.getAverage());
					switch (classLevel) {
					case 1:
						hmStat.setClass1Count(hmStat.getClass1Count() + 1);
						break;
					case 2:
						hmStat.setClass2Count(hmStat.getClass2Count() + 1);
						break;
					case 3:
						hmStat.setClass3Count(hmStat.getClass3Count() + 1);
						break;
					case 4:
						hmStat.setClass4Count(hmStat.getClass4Count() + 1);
						break;
					case 5:
						hmStat.setClass5Count(hmStat.getClass5Count() + 1);
						break;
					default:
						hmStat.setClass5Count(hmStat.getClass5Count() + 1);
						break;
					}
				}
				hmStat.setLostPercent((hmStat.getLostPercent() * hmStat.getTotalAccessCount() + hm.getLossPercent()) / (hmStat.getTotalAccessCount() + 1));
				hmStat.setTotalAccessCount(hmStat.getTotalAccessCount() + 1);
			}
			return hmStat;
		}

		return null;
	}

	public NginxMessageStat getNginxMessageStatByDateInterval(Long itemId, Long startTime, Long endTime) {
		final String hql = "from NginxMessage where itemId=" + itemId + " and createTime>=" + startTime + " and createTime<" + endTime + " and (serverIp is null or serverIp='') order by createtime asc";

		List<NginxMessage> hms = dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
		if (CollectionUtils.isNotEmpty(hms)) {
			int statusOkCount = 0, activeConnCount = 0, readingConnCount = 0, writingConnCount = 0, waitingConnCount = 0;
			float throughputRate = 0.0f;
			NginxMessageStat hmStat = new NginxMessageStat();

			for (NginxMessage hm : hms) {
				hmStat.setUserId(hm.getUserId());
				hmStat.setItemId(hm.getItemId());
				hmStat.setServerIp(hm.getServerIp());
				hmStat.setType(hm.getType());
				hmStat.setWorkerIp(hm.getWorkerIp());
				hmStat.setTimeUnitType(MessageStat.TIMEUNITTYPE_HOUR);
				hmStat.setCreateTime(CommonUtil.getLong(DateUtil.format(new Date(startTime), StatHttpMessage.mFormatDateTime)));

				if (hm.getStatus() == NginxResult.STATUS_OK) {
					statusOkCount++;
					if (hmStat.getMinThroughputRate() == 0.0f) {
						hmStat.setMinThroughputRate(hm.getThroughputRate());
					} else {
						if (hmStat.getMinThroughputRate() > hm.getThroughputRate()) {
							hmStat.setMinThroughputRate(hm.getThroughputRate());
						}
					}
					
					if (hmStat.getMaxThroughputRate() == 0.0f) {
						hmStat.setMaxThroughputRate(hm.getThroughputRate());
					} else {
						if (hmStat.getMaxThroughputRate() < hm.getThroughputRate()) {
							hmStat.setMaxThroughputRate(hm.getThroughputRate());
						}
					}
					
					throughputRate += hm.getThroughputRate();
					
					activeConnCount += hm.getActiveConn();
					readingConnCount += hm.getReadingConn();
					writingConnCount += hm.getWritingConn();
					waitingConnCount += hm.getWaitingConn();
					
					hmStat.setTotalConn(hmStat.getTotalConn() + hm.getTotalConn());
					hmStat.setTotalHandshake(hmStat.getTotalHandshake() + hm.getTotalHandshake());
					hmStat.setTotalRequest(hmStat.getTotalRequest() + hm.getTotalRequest());
				}
				
				if (statusOkCount > 0) {
					hmStat.setAverageThroughputRate(throughputRate /  statusOkCount);
					
					hmStat.setActiveConn((float)(activeConnCount * 1.0 /  statusOkCount));
					hmStat.setReadingConn((float)(readingConnCount * 1.0 /  statusOkCount));
					hmStat.setWritingConn((float)(writingConnCount * 1.0 /  statusOkCount));
					hmStat.setWaitingConn((float)(waitingConnCount * 1.0 /  statusOkCount));
				}
				
			}
			return hmStat;
		}

		return null;
	}

	public RedisMessageStat getRedisMessageStatByDateInterval(Long itemId, Long startTime, Long endTime) {
		final String hql = "from RedisMessage where itemId=" + itemId + " and createTime>=" + startTime + " and createTime<" + endTime + " and (serverIp is null or serverIp='') order by createtime asc";

		List<RedisMessage> hms = dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
		if (CollectionUtils.isNotEmpty(hms)) {
			int statusOkCount = 0, clients = 0;
			float usedMemory = 0.0f, cps = 0.0f, hitRate = 0.0f;
			RedisMessageStat hmStat = new RedisMessageStat();

			for (RedisMessage hm : hms) {
				hmStat.setUserId(hm.getUserId());
				hmStat.setItemId(hm.getItemId());
				hmStat.setServerIp(hm.getServerIp());
				hmStat.setType(hm.getType());
				hmStat.setWorkerIp(hm.getWorkerIp());
				hmStat.setTimeUnitType(MessageStat.TIMEUNITTYPE_HOUR);
				hmStat.setCreateTime(CommonUtil.getLong(DateUtil.format(new Date(startTime), StatHttpMessage.mFormatDateTime)));

				if (hm.getStatus() == NginxResult.STATUS_OK) {
					statusOkCount++;

					if (hmStat.getMinUsedMemory() == 0.0f) {
						hmStat.setMinUsedMemory(hm.getCurrentUsedMemory());
					} else {
						if (hmStat.getMinUsedMemory() > hm.getCurrentUsedMemory()) {
							hmStat.setMinUsedMemory(hm.getCurrentUsedMemory());
						}
					}

					if (hmStat.getMaxUsedMemory() == 0.0f) {
						hmStat.setMaxUsedMemory(hm.getCurrentUsedMemory());
					} else {
						if (hmStat.getMaxUsedMemory() < hm.getCurrentUsedMemory()) {
							hmStat.setMaxUsedMemory(hm.getCurrentUsedMemory());
						}
					}

					if (hmStat.getMinCPS() == 0.0f) {
						hmStat.setMinCPS(hm.getCurrentCPS());
					} else {
						if (hmStat.getMinCPS() > hm.getCurrentCPS()) {
							hmStat.setMinCPS(hm.getCurrentCPS());
						}
					}

					if (hmStat.getMaxCPS() == 0.0f) {
						hmStat.setMaxCPS(hm.getCurrentCPS());
					} else {
						if (hmStat.getMaxCPS() < hm.getCurrentCPS()) {
							hmStat.setMaxCPS(hm.getCurrentCPS());
						}
					}


					if (hmStat.getMinHitRate() == 0.0f) {
						hmStat.setMinHitRate(hm.getHitRate());
					} else {
						if (hmStat.getMinHitRate() > hm.getHitRate()) {
							hmStat.setMinHitRate(hm.getHitRate());
						}
					}

					if (hmStat.getMaxHitRate() == 0.0f) {
						hmStat.setMaxHitRate(hm.getHitRate());
					} else {
						if (hmStat.getMaxHitRate() < hm.getHitRate()) {
							hmStat.setMaxHitRate(hm.getHitRate());
						}
					}

					usedMemory += hm.getCurrentUsedMemory();
					cps += hm.getCurrentCPS();
					hitRate += hm.getHitRate();
					clients += hm.getClients();

					hmStat.setVersion(hm.getVersion());
					hmStat.setMode(hm.getMode());
					hmStat.setTotalTime(hm.getTotalTime());
				}					
				hmStat.setAvgUsedMemory((float) (usedMemory * 1.0 / statusOkCount));
				hmStat.setAvgCPS((float) (cps * 1.0 / statusOkCount));
				hmStat.setAvgHitRate((float) (hitRate * 1.0 / statusOkCount));
				hmStat.setClients((int) (clients * 1.0 / statusOkCount));
			}
			return hmStat;
		}

		return null;
	}
	
	public HttpMessageStat saveHttpMessageStat(HttpMessageStat httpMessageStat) {
		dao.getHibernateTemplate().save(httpMessageStat);
		return httpMessageStat;
	}
	
	public PingMessageStat savePingMessageStat(PingMessageStat pingMessageStat) {
		dao.getHibernateTemplate().save(pingMessageStat);
		return pingMessageStat;
	}

	public NginxMessageStat saveNginxMessageStat(NginxMessageStat nginxMessageStat) {
		dao.getHibernateTemplate().save(nginxMessageStat);
		return nginxMessageStat;
	}
	
	public RedisMessageStat saveRedisMessageStat(RedisMessageStat redisMessageStat) {
		dao.getHibernateTemplate().save(redisMessageStat);
		return redisMessageStat;
	}
	
	public PingMessage savePingMessage(PingMessage pingMessage) {
		dao.getHibernateTemplate().save(pingMessage);
		return pingMessage;
	}
	
	public NginxMessage saveNginxMessage(NginxMessage nginxMessage) {
		dao.getHibernateTemplate().save(nginxMessage);
		return nginxMessage;
	}
	
	public RedisMessage saveRedisMessage(RedisMessage redisMessage) {
		dao.getHibernateTemplate().save(redisMessage);
		return redisMessage;
	}

}
