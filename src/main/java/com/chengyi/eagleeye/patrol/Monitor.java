package com.chengyi.eagleeye.patrol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.chengyi.eagleeye.alarmsender.EmailSenderImpl;
import com.chengyi.eagleeye.alarmsender.SmsSenderImpl;
import com.chengyi.eagleeye.model.AlarmHistory;
import com.chengyi.eagleeye.model.BreakDownHistory;
import com.chengyi.eagleeye.model.DataCenterServer;
import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.model.ItemMonitor;
import com.chengyi.eagleeye.model.ItemMonitorGroup;
import com.chengyi.eagleeye.model.MonitorGroupMonitor;
import com.chengyi.eagleeye.model.assist.HttpOption;
import com.chengyi.eagleeye.network.BaseParam;
import com.chengyi.eagleeye.network.BaseResult;
import com.chengyi.eagleeye.network.http.HttpParam;
import com.chengyi.eagleeye.network.http.HttpResult;
import com.chengyi.eagleeye.network.nginx.NginxParam;
import com.chengyi.eagleeye.network.ping.PingParam;
import com.chengyi.eagleeye.network.redis.RedisParam;
import com.chengyi.eagleeye.patrol.http.HttpThread;
import com.chengyi.eagleeye.patrol.nginx.NginxThread;
import com.chengyi.eagleeye.patrol.ping.PingThread;
import com.chengyi.eagleeye.patrol.redis.RedisThread;
import com.chengyi.eagleeye.service.AlarmHistoryMgr;
import com.chengyi.eagleeye.service.BreakDownHistoryMgr;
import com.chengyi.eagleeye.service.DataCenterServerMgr;
import com.chengyi.eagleeye.service.ItemMonitorGroupMgr;
import com.chengyi.eagleeye.service.ItemMonitorMgr;
import com.chengyi.eagleeye.service.MonitorGroupMonitorMgr;
import com.chengyi.eagleeye.service.MonitorMgr;
import com.chengyi.eagleeye.util.AlarmStatus;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.CommonUtil;
import com.chengyi.eagleeye.util.NetUtil;
import com.chengyi.eagleeye.util.ServerStatus;

/**
 * @author wangzhaojun
 * 
 */
public class Monitor {
	public static Logger logger = Logger.getLogger(Monitor.class);

	private static DaoImpl dao;
	
	private static ItemMonitorMgr itemMonitorMgr;
	
	private static ItemMonitorGroupMgr itemMonitorGroupMgr;
	
	private static MonitorMgr monitorMgr;
	
	private static MonitorGroupMonitorMgr monitorGroupMonitorMgr;
	
	private static DataCenterServerMgr dataCenterServerMgr;
	
	private static AlarmHistoryMgr alarmHistoryMgr;
	
	private static BreakDownHistoryMgr breakDownHistoryMgr;
	
	private static final Monitor instance = new Monitor();
	
	public static boolean needRefresh = true;
	private static HashMap<String, Thread> threadMap = new HashMap<String, Thread>();
	
	public static Monitor getInstance() {
		return instance;
	}

	public void init() throws ServletException {
		new Thread() {
			public void run() {
				while (true) {
					if (needRefresh) {
						cleanThread();

						doInit();
						needRefresh = false;
					}
					ApplicaRuntime.sleep(2000);
				}
			}
		}.start();
	}

	private void cleanThread() {
		if (threadMap != null && threadMap.size() > 0) {
			Iterator<String> keyIt = threadMap.keySet().iterator();
			while (keyIt.hasNext()) {
				Thread thread = threadMap.get(keyIt.next());
				thread.stop();
			}
			threadMap.clear();
		}
	}
	
	public static void refershAll() {
		needRefresh = true;
		logger.info("**********************refersh all items NOW**********************");
	}
	
	@SuppressWarnings("unchecked")
	private void doInit() {
		String localIp = NetUtil.getLocalIp();
		DataCenterServer dcServer = dataCenterServerMgr.getDataCenerServer(localIp);
//		if (dcServer == null) {
//			logger.fatal("localIp:" + localIp + ", DataCenterServer is null, do nothing");
//			return;
//		}
		ApplicaRuntime.dcServer = dcServer;

		String hql = "from Item where status=1";

		List<Item> itemList = dao.getHibernateTemplate().find(hql);
		ApplicaRuntime.itemList = itemList;
		logger.info("\n\nI've got " + itemList.size() + " item(s) from CENTER:" + itemList + ", localIp:" + localIp + ", port:" + ApplicaRuntime.port + "\n\n");
		if (CollectionUtils.isEmpty(itemList)) {
			return;
		}
		
		boolean statFlag = true;
		if (localIp != null && (localIp.contains("32.119") || localIp.contains("32.67"))) {
			if (statFlag) 
				dostat();
			
			for (int i = 0; i < itemList.size(); i++) {
				Item wd = itemList.get(i);
//				if (wd.getType() == Item.TYPE_REDIS || wd.getType() == Item.TYPE_NGINX)
					monitor(wd);
			}
		} else {
			if (statFlag) 
				dostat();
			
			for (int i = 0; i < itemList.size(); i++) {
				Item wd = itemList.get(i);
//				if (wd.getUri().contains("119.ku6"))
//				if (wd.getId() == 231L || wd.getId() == 228L)
//				if (wd.getId() == 176L)
//				if (wd.getId() == 251L)
//				if (wd.getType() == Item.TYPE_REDIS)
//				if (wd.getId() == 219L || wd.getId() == 82L)
//				if (wd.getId() == 218L)
//				if (wd.getUserId() == 20L)
//					monitor(wd);
			}
		}
	}
	
	private static void dostat() {
		// TODO
	}
	
	private static void monitor(final Item item) {
		if (item.getType() == Item.TYPE_HTTP) {
			JSONObject jsonObj = JSONObject.fromObject(item.getOptions());
			
			HttpOption httpOptions = (HttpOption) JSONObject.toBean(jsonObj, HttpOption.class);
			String serverIps = (httpOptions == null ? "" : httpOptions.getServerIps());
			
			if (StringUtils.isNotEmpty(serverIps)) {
				String[] serverArr = serverIps.split(";");
				for (int i = 0; i < serverArr.length; i++) {
					final String serverIp = serverArr[i];

					HttpParam httpParam = new HttpParam(item.getId(), item.getUri(), serverIp, httpOptions.getHttpTimeout(), httpOptions.getResultMatchPattern(), httpOptions.getResultMatchPatternStatus());
					Thread thread = new Thread(new HttpThread(item, httpParam));
					thread.setName(item.getId() + "-" + serverIp);
					threadMap.put(thread.getName(), thread);
					thread.start();
				}
			} else {
				HttpParam httpParam = new HttpParam(item.getId(), item.getUri(), null, httpOptions.getHttpTimeout(), httpOptions.getResultMatchPattern(), httpOptions.getResultMatchPatternStatus());
				Thread thread = new Thread(new HttpThread(item, httpParam));
				thread.setName(item.getId().toString());
				threadMap.put(thread.getName(), thread);
				thread.start();
			}
		} else if (item.getType() == Item.TYPE_PING) {
			PingParam pingParam = new PingParam(item.getId(), item.getUri(), null);
			Thread thread = new Thread(new PingThread(item, pingParam));
			thread.setName(item.getId().toString());
			threadMap.put(thread.getName(), thread);
			thread.start();
		} else if (item.getType() == Item.TYPE_NGINX) {
			NginxParam nginxParam = new NginxParam(item.getId(), item.getUri(), null);
			Thread thread = new Thread(new NginxThread(item, nginxParam));
			thread.setName(item.getId().toString());
			threadMap.put(thread.getName(), thread);
			thread.start();
		} else if (item.getType() == Item.TYPE_REDIS) {
			RedisParam redisParam = new RedisParam(item.getId(), item.getUri(), item.getOptions());
			Thread thread = new Thread(new RedisThread(item, redisParam));
			thread.setName(item.getId().toString());
			threadMap.put(thread.getName(), thread);
			thread.start();
		}
	}
	
	public void alarm(Item item, BaseParam httpParam) {
		BaseResult lastErrorHttpResult = httpParam.getLastErrorResult(); 
		String key = getLastAlarmTimeKey(item, httpParam);
		if ((httpParam.getFailTimes() > item.getRetryTimes()) && (item.getRetryTimes() > httpParam.getSuccTimes())) { // DOWN
			Long lastAlarmTime = RedisUtil.getLong(key);
			if (lastAlarmTime != null && lastAlarmTime == -1L) lastAlarmTime = RedisUtil.getLong(key); // timeout, try again
			logger.info("lastAlarmTime:" + lastAlarmTime + ", serverStatus:" + httpParam.getServerStatus() + ", \t" + httpParam.getUri() + "key:" + key + ", item:" + item + ", httpResult:" + lastErrorHttpResult);

			if (lastAlarmTime == null || lastAlarmTime == 0L || System.currentTimeMillis() - lastAlarmTime > item.getContinuousReminder() * 1000L) { // 连续告警提醒
				if (lastAlarmTime != null) {
					logger.info("lastAlarmTime:" + lastAlarmTime + ", \t (System.currentTimeMillis() - lastAlarmTime):" + (System.currentTimeMillis() - lastAlarmTime) + "\t (item.getContinuousReminder() * 1000L):" + (item.getContinuousReminder() * 1000L) + ", item:" + item);
				}
				httpParam.setSuccTimes(0);
				sendNotice(item, httpParam, ServerStatus.DOWN);
			}
			httpParam.add2Queue(-1);
		} else {  // OK
			long serverStatus = httpParam.getServerStatus();
			logger.info("serverStatus:" + serverStatus + ", \t" + httpParam.getUri());

			if (serverStatus == ServerStatus.DOWN) {
				httpParam.setFailTimes(0);
				sendNotice(item, httpParam, ServerStatus.OK);
			}
			httpParam.add2Queue(1);
		}
	}
	
	private String getLastAlarmTimeKey(Item item, BaseParam baseParam) {
		return item.getId() + "-" + baseParam.getBindAddress() + "-" + ApplicaRuntime.globalFlag;
	}
	
	private String getServerStatusKey(Long itemId) {
		return itemId + "_status_" + ApplicaRuntime.globalFlag;
	}
	
	private Map<Long, com.chengyi.eagleeye.model.Monitor> userMap = new HashMap<Long, com.chengyi.eagleeye.model.Monitor>();

	private void sendNotice(Item item, BaseParam httpParam,  long serverStatus) {
		BaseResult httpResult = httpParam.getLastErrorResult();
		String lastAlarmTimeKey = getLastAlarmTimeKey(item, httpParam);
		String serverStatusKey = getServerStatusKey(item.getId());
		logger.info("*****************set lastAlarmTime:" + System.currentTimeMillis() + ", \t" + httpParam.getUri() + "key:" + lastAlarmTimeKey);
		httpParam.setServerStatus(serverStatus);
		String msg = "";
		if (serverStatus == ServerStatus.DOWN) {
			msg = "DOWN!!!";
			httpParam.alarmStatus = AlarmStatus.DOWN;
			
			RedisUtil.setLong(lastAlarmTimeKey, System.currentTimeMillis());
			RedisUtil.setLong(serverStatusKey, ServerStatus.DOWN); // down
		} else if (serverStatus == ServerStatus.OK) {
			msg = "OK!";
			httpParam.alarmStatus = AlarmStatus.OK;
			
			RedisUtil.setLong(lastAlarmTimeKey, 0L);
			RedisUtil.setLong(serverStatusKey, ServerStatus.OK); // ok
		}
		int sum = httpParam.sumQueueChangeCount();
		if (sum >= 6) {
			logger.info("sumQueueChangeCount:" + sum);
			msg = "INSTABLE";
			httpParam.alarmStatus = AlarmStatus.INSTABLE;
			
			RedisUtil.setLong(lastAlarmTimeKey, System.currentTimeMillis());
			RedisUtil.setLong(serverStatusKey, ServerStatus.INSTABLE); // INSTABLE
		}
		String target = item.getName();
		if (StringUtils.isNotEmpty(httpParam.getBindAddress())) {
			target += "[" + httpParam.getBindAddress() + "]";
		}
		logger.info("item: " + target + " is " + msg + " [failTimes:" + httpParam.getFailTimes() + "], " + "[succTimes:" + httpParam.getSuccTimes() + "], [serverStatus:" + serverStatus + "], " + httpParam.getUri());

		// send msg
		String content = target + " is " + msg;
		List<ItemMonitor> userList = itemMonitorMgr.getItemAlarmsByItemId(item.getId());
		
		// write BreakDown history
		BreakDownHistory breakDownHistory = writeBreakDownHistory(item.getUserId(), item.getId(), httpParam.getBindAddress(), serverStatus, httpResult);
		Long breakDownId = null;
		if (breakDownHistory != null) {
			breakDownId = breakDownHistory.getId();
		}
		long eventSeqId = RedisUtil.getCurrentEventSeqId();
		
		for (ItemMonitor itemMonitor : userList) {
			Long monitorId = itemMonitor.getMonitorId();
			byte alarmChannel = itemMonitor.getAlarmChannel();
			
			com.chengyi.eagleeye.model.Monitor monitor = userMap.get(monitorId);
			if (monitor == null) {
				monitor = monitorMgr.getMonitor(monitorId);
				if (monitor == null) {
					logger.error("item:" + item + ", corresponding user is not exist, monitorId:" + monitorId);
					continue;
				} else {
					userMap.put(monitorId, monitor);
				}
			}
			
			logger.info("content:" + content + ", alarmChannel:" + alarmChannel + ", " + httpParam.getUri());
			if ((alarmChannel & ApplicaRuntime.ALARMCHANNEL_EMAIL) == ApplicaRuntime.ALARMCHANNEL_EMAIL) {
				logger.error("send email to " + monitor.getEmail() + ", content:" + content + ", " + httpParam.getUri());
				EmailSenderImpl.getInstance().send(monitor.getEmail(), content);
				writeAlarmHistory(item.getUserId(), item.getId(), httpParam.getBindAddress(), breakDownId, monitor.getId(), ApplicaRuntime.ALARMCHANNEL_EMAIL, monitor.getEmail(), content, eventSeqId, serverStatus, httpResult);
			}
			if ((alarmChannel & ApplicaRuntime.ALARMCHANNEL_SMS) == ApplicaRuntime.ALARMCHANNEL_SMS) {
				logger.error("send sms to " + monitor.getCellphone() + ", content:" + content + ", " + httpParam.getUri());
				SmsSenderImpl.getInstance().send(monitor.getCellphone(), content);
				writeAlarmHistory(item.getUserId(), item.getId(), httpParam.getBindAddress(), breakDownId, monitor.getId(), ApplicaRuntime.ALARMCHANNEL_SMS, monitor.getCellphone(), content, eventSeqId, serverStatus, httpResult);
			}
			if ((alarmChannel & ApplicaRuntime.ALARMCHANNEL_YOUNI) == ApplicaRuntime.ALARMCHANNEL_YOUNI) {
				logger.error("send youni to " + monitor.getCellphone() + ", content:" + content + ", " + httpParam.getUri());
//				SndaYouniSender.getInstance().send(monitor.getCellphone(), content);
				writeAlarmHistory(item.getUserId(), item.getId(), httpParam.getBindAddress(), breakDownId, monitor.getId(), ApplicaRuntime.ALARMCHANNEL_YOUNI, monitor.getCellphone(), content, eventSeqId, serverStatus, httpResult);
			}
		}

		// detect monitorGroup
		List<ItemMonitorGroup> imgs = itemMonitorGroupMgr.getItemMonitorGroupsByItemId(item.getId());
		logger.info("imgs:" + imgs);
		if (CollectionUtils.isNotEmpty(imgs)) {
			for (ItemMonitorGroup mgm : imgs) {
				Long monitorGroupId = mgm.getMonitorGroupId();
				Long monitorsTotalCount = monitorGroupMonitorMgr.countMonitorsByGroupId(monitorGroupId);
				Long monitorTotalPage = CommonUtil.getTotalPage(monitorsTotalCount, CommonUtil.pageSize);
				for (Long p = 1L; p <= monitorTotalPage; p++) {
					List<MonitorGroupMonitor> mgms = monitorGroupMonitorMgr.getMonitorsByGroupId(monitorGroupId, p);
					if (CollectionUtils.isNotEmpty(mgms)) {
						for (MonitorGroupMonitor element: mgms) {
							Long monitorId = element.getMonitorId();
							byte alarmChannel = element.getAlarmChannel();
							com.chengyi.eagleeye.model.Monitor monitor = monitorMgr.getMonitor(monitorId);
							if (monitor != null) {
								if ((alarmChannel & ApplicaRuntime.ALARMCHANNEL_EMAIL) == ApplicaRuntime.ALARMCHANNEL_EMAIL) {
									EmailSenderImpl.getInstance().send(monitor.getEmail(), content);
									writeAlarmHistory(item.getUserId(), item.getId(), httpParam.getBindAddress(), breakDownId, monitor.getId(), ApplicaRuntime.ALARMCHANNEL_EMAIL, monitor.getEmail(), content, eventSeqId, serverStatus, httpResult);
								}
								if ((alarmChannel & ApplicaRuntime.ALARMCHANNEL_SMS) == ApplicaRuntime.ALARMCHANNEL_SMS) {
									SmsSenderImpl.getInstance().send(monitor.getCellphone(), content);
									writeAlarmHistory(item.getUserId(), item.getId(), httpParam.getBindAddress(), breakDownId, monitor.getId(), ApplicaRuntime.ALARMCHANNEL_SMS, monitor.getCellphone(), content, eventSeqId, serverStatus, httpResult);
								}
								if ((alarmChannel & ApplicaRuntime.ALARMCHANNEL_YOUNI) == ApplicaRuntime.ALARMCHANNEL_YOUNI) {
//									SndaYouniSender.getInstance().send(monitor.getCellphone(), content);
									writeAlarmHistory(item.getUserId(), item.getId(), httpParam.getBindAddress(), breakDownId, monitor.getId(), ApplicaRuntime.ALARMCHANNEL_YOUNI, monitor.getCellphone(), content, eventSeqId, serverStatus, httpResult);
								}
							}
						}
					}
				}
			}
		}
		httpParam.setSuccTimes(0);
		httpParam.setFailTimes(0);
	}
	
	private void writeAlarmHistory(Long itemUserId, Long itemId, String serverIp, Long breakDownId, Long monitorId, byte alarmChannel, String monitorAddr, String content, long seqId, long serverStatus, BaseResult httpResult) {
		AlarmHistory alarmHistory = new AlarmHistory();

		alarmHistory.setUserId(itemUserId);
		alarmHistory.setItemId(itemId);
		alarmHistory.setServerIp(serverIp);
		alarmHistory.setBreakDownId(breakDownId);
		alarmHistory.setAlarmChannel(alarmChannel);

		if (serverStatus == ServerStatus.OK) {
			alarmHistory.setErrorType(HttpResult.STATUS_OK);
		} else {
			alarmHistory.setErrorType(httpResult.getErrorTypeByResult(httpResult));
		}
		
		alarmHistory.setMonitorId(monitorId);
		alarmHistory.setMonitorAddr(monitorAddr);
		alarmHistory.setContent(content);
		alarmHistory.setEventSeqId(seqId);
		alarmHistory.setCreateTime(System.currentTimeMillis());

		alarmHistory = alarmHistoryMgr.saveAlarmHistory(alarmHistory);
		logger.info("alarmHistory:" + alarmHistory);
	}
	
	private BreakDownHistory writeBreakDownHistory(Long itemUserId, Long itemId, String serverIp, long serverStatus, BaseResult httpResult) {
		String key = itemId + "-" + serverIp;
		BreakDownHistory breakDownHistory = (BreakDownHistory) RedisUtil.get(key);
		
		logger.info("itemId:" + itemId + ", serverIP:" + serverIp + ", breakDownHistoryInRedis:" + breakDownHistory);
		if (breakDownHistory == null) {
			if (serverStatus == ServerStatus.DOWN) {
				breakDownHistory = new BreakDownHistory();
				
				long nowTime = System.currentTimeMillis();
				breakDownHistory.setUserId(itemUserId);
				breakDownHistory.setItemId(itemId);
				breakDownHistory.setServerIp(serverIp);
				breakDownHistory.setStartTime(nowTime);
				breakDownHistory.setErrorType(httpResult.getErrorTypeByResult(httpResult));
				
				breakDownHistory.setSendAlarm(true);
				breakDownHistory.setCreateTime(nowTime);
				
				breakDownHistory = breakDownHistoryMgr.saveBreakDownHistory(breakDownHistory);
				RedisUtil.save(key, breakDownHistory);
				
				logger.info("itemId:" + itemId + ", serverIP:" + serverIp + ", save breakDownHistory:" + breakDownHistory);
			}
		} else {
			if (serverStatus == ServerStatus.OK) {
				breakDownHistory.setEndTime(System.currentTimeMillis());
				
				breakDownHistory = breakDownHistoryMgr.updateBreakDownHistory(breakDownHistory);
				RedisUtil.remove(key);
				
				logger.info("itemId:" + itemId + ", serverIP:" + serverIp + ", update breakDownHistory:" + breakDownHistory);
			}
		}

		if (breakDownHistory == null) {
			logger.error("breakDownHistory is null, itemUserId:" + itemUserId + ", itemId:" + itemId + ", serverIp:" + serverIp + ", serverStatus:" + serverStatus );
		}
		return breakDownHistory;
	}
	
	public static void setItemMonitorMgr(ItemMonitorMgr itemMonitorMgr) {
		Monitor.itemMonitorMgr = itemMonitorMgr;
	}

	public static void setItemMonitorGroupMgr(ItemMonitorGroupMgr itemMonitorGroupMgr) {
		Monitor.itemMonitorGroupMgr = itemMonitorGroupMgr;
	}

	public static void setMonitorMgr(MonitorMgr monitorMgr) {
		Monitor.monitorMgr = monitorMgr;
	}

	public static void setMonitorGroupMonitorMgr(MonitorGroupMonitorMgr monitorGroupMonitorMgr) {
		Monitor.monitorGroupMonitorMgr = monitorGroupMonitorMgr;
	}

	public static void setDataCenterServerMgr(DataCenterServerMgr dataCenterServerMgr) {
		Monitor.dataCenterServerMgr = dataCenterServerMgr;
	}
	
	public static void setAlarmHistoryMgr(AlarmHistoryMgr alarmHistoryMgr) {
		Monitor.alarmHistoryMgr = alarmHistoryMgr;
	}
	
	public static void setBreakDownHistoryMgr(BreakDownHistoryMgr breakDownHistoryMgr) {
		Monitor.breakDownHistoryMgr = breakDownHistoryMgr;
	}
	
	public static void setDao(DaoImpl dao) {
		Monitor.dao = dao;
	}
}

