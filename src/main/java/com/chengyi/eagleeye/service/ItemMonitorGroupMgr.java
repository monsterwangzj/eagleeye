package com.chengyi.eagleeye.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.chengyi.eagleeye.model.ItemMonitorGroup;
import com.chengyi.eagleeye.patrol.DaoImpl;

/**
 * @author wangzhaojun
 * 
 */
public class ItemMonitorGroupMgr {

	private static Logger logger = Logger.getLogger(ItemMonitorGroupMgr.class);

	@Autowired
	private DaoImpl dao;
	
	public ItemMonitorGroup saveItemMonitorGroup(ItemMonitorGroup itemMonitorGroup) {
		dao.getHibernateTemplate().save(itemMonitorGroup);
		return itemMonitorGroup;
	}
	
	public ItemMonitorGroup updateItemMonitorGroup(ItemMonitorGroup itemMonitorGroup) {
		dao.getHibernateTemplate().update(itemMonitorGroup);
		return itemMonitorGroup;
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemMonitorGroup> getItemMonitorGroupsByItemId(Long itemId) {
		String hql = "from ItemMonitorGroup where itemId=" + itemId;
		List<ItemMonitorGroup> itemMonitorGroupList = dao.getHibernateTemplate().find(hql);
		logger.info(itemMonitorGroupList);
		return itemMonitorGroupList;
	}

	public void deleteItemMonitorGroup(Long id) {
		ItemMonitorGroup itemMonitorGroup = dao.getHibernateTemplate().load(ItemMonitorGroup.class, id);
		dao.getHibernateTemplate().delete(itemMonitorGroup);
	}

}
