package com.chengyi.eagleeye.service;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.chengyi.eagleeye.model.MonitorGroupMonitor;
import com.chengyi.eagleeye.patrol.DaoImpl;
import com.chengyi.eagleeye.util.CommonUtil;

/**
 * @author wangzhaojun
 * 
 */
@SuppressWarnings("unchecked")
public class MonitorGroupMonitorMgr {

	@Autowired
	private DaoImpl dao;

	public MonitorGroupMonitor getMonitorGroupMonitor(Long id) {
		return (MonitorGroupMonitor) dao.getHibernateTemplate().get(MonitorGroupMonitor.class, id);
	}

	public MonitorGroupMonitor getMonitorGroupMonitor(Long monitorGroupId, Long monitorId) {
		String hql = "from MonitorGroupMonitor where monitorGroupId=" + monitorGroupId + " and monitorId=" + monitorId;
		List<MonitorGroupMonitor> monitorGroupMonitorList = dao.getHibernateTemplate().find(hql);
		if (CollectionUtils.isNotEmpty(monitorGroupMonitorList)) {
			return monitorGroupMonitorList.get(0);
		} else {
			return null;
		}
	}

	public MonitorGroupMonitor saveMonitorGroupMonitor(MonitorGroupMonitor monitorGroupMonitor) {
		dao.getHibernateTemplate().save(monitorGroupMonitor);
		return monitorGroupMonitor;
	}

	public MonitorGroupMonitor updateMonitorGroupMonitor(MonitorGroupMonitor monitorGroupMonitor) {
		dao.getHibernateTemplate().update(monitorGroupMonitor);
		return monitorGroupMonitor;
	}

	public void deleteMonitorGroupMonitor(Long id) {
		MonitorGroupMonitor monitorGroupMonitor = dao.getHibernateTemplate().load(MonitorGroupMonitor.class, id);
		dao.getHibernateTemplate().delete(monitorGroupMonitor);
	}

	public void deleteMonitorGroupMonitorByGid(Long gid) {
		String hql = "delete from MonitorGroupMonitor where monitorGroupId=" + gid;
		dao.getHibernateTemplate().bulkUpdate(hql);
	}

	
	public List<MonitorGroupMonitor> getMonitorsByGroupId(Long groupId, Long page) {
		final String hql = "from MonitorGroupMonitor where monitorGroupId=" + groupId + " order by createTime desc";
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

	public Long countMonitorsByGroupId(Long groupId) {
		return (Long) dao.getHibernateTemplate().find("select count(*) from MonitorGroupMonitor where monitorGroupId=" + groupId).iterator().next();
	}
	
	public Long countMonitorGroupMonitorsByMonitorId(Long monitorId) {
		return (Long) dao.getHibernateTemplate().find("select count(*) from MonitorGroupMonitor where monitorId=" + monitorId).iterator().next();
	}

	public List<Long> getMonitorGroupMonitorIdsByMonitorId(Long monitorId, Long page) {
		final String hql = "select id from MonitorGroupMonitor where monitorId=" + monitorId + " order by createTime desc";
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
}
