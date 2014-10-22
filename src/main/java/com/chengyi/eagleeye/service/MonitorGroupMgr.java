package com.chengyi.eagleeye.service;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.chengyi.eagleeye.model.MonitorGroup;
import com.chengyi.eagleeye.patrol.DaoImpl;
import com.chengyi.eagleeye.util.CommonUtil;

/**
 * @author wangzhaojun
 * 
 */
@SuppressWarnings("unchecked")
public class MonitorGroupMgr {
	
	@Autowired
	private DaoImpl dao;

	public MonitorGroup getMonitorGroup(Long id) {
		return (MonitorGroup) dao.getHibernateTemplate().get(MonitorGroup.class, id);
	}
	
	public MonitorGroup saveUserGroup(MonitorGroup userGroup) {
		dao.getHibernateTemplate().save(userGroup);
		return userGroup;
	}

	public MonitorGroup updateMonitorGroup(MonitorGroup monitorGroup) {
		dao.getHibernateTemplate().update(monitorGroup);
		return monitorGroup;
	}
	
	public void deleteMonitorGroup(Long id) {
		MonitorGroup monitorGroup = dao.getHibernateTemplate().load(MonitorGroup.class, id);
		dao.getHibernateTemplate().delete(monitorGroup);
	}
	
	public List<MonitorGroup> getFaultHandleGroups(Long userId) {
		String hql = "from MonitorGroup where userId=" + userId + " order by type desc, id desc";
		return (List<MonitorGroup>) dao.getHibernateTemplate().find(hql);
	}
	
	public List<MonitorGroup> getMonitorGroupsByUserId(Long userId, Long page) {
		final String hql = "from MonitorGroup where userId=" + userId + " order by type desc, id desc";
		final Long p = page;
		return dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				query.setFirstResult((p.intValue() - 1) * CommonUtil.pageSize);
				query.setMaxResults(CommonUtil.pageSize);
				return query.list();
			}
		});
	}
	
	public List<MonitorGroup> getAllMonitorGroups(Long userId) {
		return getFaultHandleGroups(userId);
	}
	
	public Long countMonitorGroupByUserId(Long userId) {
		return (Long) dao.getHibernateTemplate().find("select count(*) from MonitorGroup where userId=" + userId).iterator().next();
	}
	
	public boolean isDefaultMonitorGroupExists(Long userId) {
		long num = (Long) dao.getHibernateTemplate().find("select count(*) from MonitorGroup where userId=" + userId + " and type=1").iterator().next();
		return (num >= 1);
	}
	
	public MonitorGroup getDefaultMonitorGroup(Long userId) {
		String hql = "from MonitorGroup where userId='" + userId + "' and type=1";
		List<MonitorGroup> groups = dao.getHibernateTemplate().find(hql);
		if (CollectionUtils.isNotEmpty(groups)) {
			return groups.get(0);
		} else {
			return null;
		}
	}
	
}
