package com.chengyi.eagleeye.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.chengyi.eagleeye.model.ItemMonitor;
import com.chengyi.eagleeye.patrol.DaoImpl;

/**
 * @author wangzhaojun
 * 
 */
public class ItemMonitorMgr {

	private static Logger logger = Logger.getLogger(ItemMonitorMgr.class);
	
	@Autowired
	private DaoImpl dao;

	@SuppressWarnings("unchecked")
	public List<ItemMonitor> getItemAlarmsByItemId(Long itemId) {
		String hql = "from ItemMonitor where itemId=" + itemId;
		List<ItemMonitor> itemAlarmList = dao.getHibernateTemplate().find(hql);
		logger.info(itemAlarmList);
		return itemAlarmList;
	}

}
