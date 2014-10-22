package com.chengyi.eagleeye.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.chengyi.eagleeye.model.ItemGroup;
import com.chengyi.eagleeye.patrol.DaoImpl;

/**
 * @author wangzhaojun
 * 
 */
public class ItemGroupMgr {
	private static Logger logger = Logger.getLogger(ItemGroupMgr.class);

	@Autowired
	private DaoImpl dao;

	public ItemGroup getItemGroup(Long id) {
		return (ItemGroup) dao.getHibernateTemplate().get(ItemGroup.class, id);
	}

	public ItemGroup updateItemGroup(ItemGroup itemGroup) {
		dao.getHibernateTemplate().update(itemGroup);
		return itemGroup;
	}

	public void deleteItemGroup(Long gid) {
		ItemGroup itemGroup = dao.getHibernateTemplate().load(ItemGroup.class, gid);
		dao.getHibernateTemplate().delete(itemGroup);
	}

	public ItemGroup saveItemGroup(ItemGroup itemGroup) {
		dao.getHibernateTemplate().save(itemGroup);
		return itemGroup;
	}

	public Integer countItemGroupByUserId(Long userId) {
		return (Integer) dao.getHibernateTemplate().find("select count(*) from ItemGroup where userId=" + userId + " and status=1").iterator().next();
	}

	@SuppressWarnings("unchecked")
	public List<ItemGroup> getItemGroupsByUserId(Long userId) {
		return (List<ItemGroup>) dao.getHibernateTemplate().find("from ItemGroup where userId=" + userId + " and status=1");
	}

}
