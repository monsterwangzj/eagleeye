package com.chengyi.eagleeye.service;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.chengyi.eagleeye.model.Monitor;
import com.chengyi.eagleeye.patrol.DaoImpl;
import com.chengyi.eagleeye.util.CommonUtil;

/**
 * @author wangzhaojun
 * 
 */
@SuppressWarnings("unchecked")
public class MonitorMgr {
	private static Logger logger = Logger.getLogger(MonitorMgr.class);

	@Autowired
	private DaoImpl dao;

	public Monitor saveMonitor(Monitor monitor) {
		dao.getHibernateTemplate().save(monitor);
		return monitor;
	}

	public Monitor updateMonitor(Monitor monitor) {
		dao.getHibernateTemplate().update(monitor);
		return monitor;
	}

	public void delMonitor(Long id) {
		Monitor monitor = dao.getHibernateTemplate().load(Monitor.class, id);
		dao.getHibernateTemplate().delete(monitor);
	}

	public Monitor getMonitor(Long id) {
		return (Monitor) dao.getHibernateTemplate().get(Monitor.class, id);
	}

	public List<Monitor> getFaultHandleUsers(long userId, long page) {
		final String hql = "from Monitor where userId=" + userId + " order by createtime desc";
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

	public Long countMonitorsByUserId(Long userId) {
		return (Long) dao.getHibernateTemplate().find("select count(*) from Monitor where userId=" + userId).iterator().next();
	}

	public Monitor getMonitorByNamenId(String name, Long userId) {
		String hql = "from Monitor where name='" + name + "' and userId='" + userId + "'";
		List<Monitor> userList = dao.getHibernateTemplate().find(hql);
		if (CollectionUtils.isNotEmpty(userList)) {
			return userList.get(0);
		} else {
			return null;
		}
	}
	
	public List<Monitor> getMonitorsByUserId(Long userId) {
		String hql = "from Monitor where userId='" + userId + "'";
		return dao.getHibernateTemplate().find(hql);
	}
	
}
