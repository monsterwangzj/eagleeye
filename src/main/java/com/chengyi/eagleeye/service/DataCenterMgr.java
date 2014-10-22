package com.chengyi.eagleeye.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.chengyi.eagleeye.model.DataCenter;
import com.chengyi.eagleeye.patrol.DaoImpl;

/**
 * @author wangzhaojun
 * 
 */
public class DataCenterMgr {
	private static Logger logger = Logger.getLogger(DataCenterMgr.class);

	@Autowired
	private DaoImpl dao;

	public DataCenter getDataCener(Long id) {
		return (DataCenter) dao.getHibernateTemplate().get(DataCenter.class, id);
	}

}
