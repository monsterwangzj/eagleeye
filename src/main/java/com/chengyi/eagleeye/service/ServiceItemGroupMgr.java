package com.chengyi.eagleeye.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.chengyi.eagleeye.model.ServiceItemGroup;
import com.chengyi.eagleeye.patrol.DaoImpl;

/**
 * @author wangzhaojun
 * 
 */
public class ServiceItemGroupMgr {

	@Autowired
	private DaoImpl dao;

	public ServiceItemGroup getServiceItemGroup(Long id) {
		return (ServiceItemGroup) dao.getHibernateTemplate().get(ServiceItemGroup.class, id);
	}

	public ServiceItemGroup updateServiceItemGroup(ServiceItemGroup serviceItemGroup) {
		dao.getHibernateTemplate().update(serviceItemGroup);
		return serviceItemGroup;
	}

	public void deleteServiceItemGroup(Long gid) {
		ServiceItemGroup serviceItemGroup = dao.getHibernateTemplate().load(ServiceItemGroup.class, gid);
		dao.getHibernateTemplate().delete(serviceItemGroup);
	}

	public ServiceItemGroup saveServiceItemGroup(ServiceItemGroup serviceItemGroup) {
		dao.getHibernateTemplate().save(serviceItemGroup);
		return serviceItemGroup;
	}

	public Integer countServiceItemGroupByUserId(Long userId) {
		return (Integer) dao.getHibernateTemplate().find("select count(*) from ServiceItemGroup where userId=" + userId + " and status=1").iterator().next();
	}

	@SuppressWarnings("unchecked")
	public List<ServiceItemGroup> getServiceItemGroupsByUserId(Long userId) {
		return (List<ServiceItemGroup>) dao.getHibernateTemplate().find("from ServiceItemGroup where userId=" + userId + " and status=1");
	}

}
