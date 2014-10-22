package com.chengyi.eagleeye.model.assist;

import com.chengyi.eagleeye.model.message.http.HttpMessage;
import com.chengyi.eagleeye.model.message.http.HttpMessageStat;
import com.chengyi.eagleeye.network.http.HttpResult;

public class HttpHelper {
	public static HttpMessageStat  getHttpMessageStat(HttpMessage hm, int monitorFreq) {
		HttpMessageStat hmStat = new HttpMessageStat();
		hmStat.setId(hm.getId());
		hmStat.setUserId(hm.getUserId());
		hmStat.setItemId(hm.getItemId());
		hmStat.setServerIp(hm.getServerIp());
		hmStat.setType(hm.getType());
		hmStat.setWorkerIp(hm.getWorkerIp());
		if (monitorFreq >= 60) {
			hmStat.setTimeUnitType(HttpMessageStat.TIMEUNITTYPE_MINUTE);
		}
		if (monitorFreq >= 3600) { 
			hmStat.setTimeUnitType(HttpMessageStat.TIMEUNITTYPE_HOUR);
		}
		hmStat.setCreateTime(hm.getCreateTime());
		hmStat.setTotalAccessCount(1L);
		
		if (hm.getStatus() == HttpResult.STATUS_OK) {
			hmStat.setSuccAccessCount(1L);
			hmStat.setSuccAccessCostTime(hm.getTotalTime());
			hmStat.setSuccDnsLookupTime(hm.getDnsLookupTime());
			hmStat.setSuccConnectingTime(hm.getConnectingTime());
			hmStat.setSuccWaitingTime(hm.getWaitingTime());
			hmStat.setSuccReceivingTime(hm.getReceivingTime());
			
			hmStat.setMinResponseTime(hm.getTotalTime());
			hmStat.setMaxResponseTime(hm.getTotalTime());
			int classLevel = HttpMessageStat.getClassByResponseTime(hm.getTotalTime());
			switch (classLevel) {
			case 1:
				hmStat.setClass1Count(1);
				break;
			case 2:
				hmStat.setClass2Count(1);
				break;
			case 3:
				hmStat.setClass3Count(1);
				break;
			case 4:
				hmStat.setClass4Count(1);
				break;
			case 5:
				hmStat.setClass5Count(1);
				break;
			case 6:
				hmStat.setClass6Count(1);
				break;
			default:
				hmStat.setClass6Count(1);
				break;
			}
		} else {
			hmStat.setSuccAccessCount(0L);
			hmStat.setSuccAccessCostTime(0L);
			hmStat.setSuccDnsLookupTime(0L);
			hmStat.setSuccConnectingTime(0L);
			hmStat.setSuccWaitingTime(0L);
			hmStat.setSuccReceivingTime(0L);
		}
		
		return hmStat;
	}
}
