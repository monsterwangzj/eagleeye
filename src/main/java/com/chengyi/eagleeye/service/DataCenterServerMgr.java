/**
 * 
 */
package com.chengyi.eagleeye.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.chengyi.eagleeye.model.DataCenterServer;
import com.chengyi.eagleeye.patrol.DaoImpl;

/**
 * @author wangzhaojun
 * 
 */
public class DataCenterServerMgr {
	private static Logger logger = Logger.getLogger(DataCenterServerMgr.class);
	
	@Autowired
	private DaoImpl dao;

	@SuppressWarnings("unchecked")
	public DataCenterServer getDataCenerServer(String ip) {
		String hql = "from DataCenterServer where ip='" + ip + "'";
		List<DataCenterServer> itemList = dao.getHibernateTemplate().find(hql);
		if (CollectionUtils.isNotEmpty(itemList)) {
			return itemList.get(0);
		} else {
			return null;
		}
	}

}
