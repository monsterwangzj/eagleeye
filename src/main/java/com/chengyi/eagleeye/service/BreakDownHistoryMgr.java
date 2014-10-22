package com.chengyi.eagleeye.service;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.chengyi.eagleeye.model.BreakDownHistory;
import com.chengyi.eagleeye.patrol.DaoImpl;
import com.chengyi.eagleeye.util.CommonUtil;

/**
 * @author wangzhaojun
 * 
 */
public class BreakDownHistoryMgr {
	@Autowired
	private DaoImpl dao;

	public BreakDownHistory saveBreakDownHistory(BreakDownHistory breakDownHistory) {
		dao.getHibernateTemplate().save(breakDownHistory);
		return breakDownHistory;
	}

	public BreakDownHistory updateBreakDownHistory(BreakDownHistory breakDownHistory) {
		dao.getHibernateTemplate().update(breakDownHistory);
		return breakDownHistory;
	}

	public List<BreakDownHistory> getBreakDownHistorys(Long userId) {
		String hql = "from BreakDownHistory where userid='" + userId + "' AND endtime=0 ORDER BY id DESC limit 10";
		List<BreakDownHistory> itemList = dao.getHibernateTemplate().find(hql);
		return itemList;
	}

	public List<BreakDownHistory> getNotResumeBreakDownHistorys(Long userId) {
		String hql = "from BreakDownHistory where userid='" + userId + "' AND endtime!=0 ORDER BY id DESC limit 10";
		List<BreakDownHistory> itemList = dao.getHibernateTemplate().find(hql);
		return itemList;
	}

	@SuppressWarnings("unchecked")
	public List<BreakDownHistory> getBreakDownHistorysByUserIdnCreateTime(Long userId, long startTime, long endTime, Long pageNo) {
		final String hql = "from BreakDownHistory where userId=" + userId + " and createTime >= " + startTime + " and createTime < " + endTime + " order by id desc ";
		final Long p = pageNo;
		return dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				query.setFirstResult((p.intValue() - 1) * CommonUtil.pageSize);
				query.setMaxResults(CommonUtil.pageSize);
				return query.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<BreakDownHistory> getNotResumeItemsByUserId(Long userId, long startTime, long endTime, Long pageNo) {
//		final String hql = "from BreakDownHistory where userId=" + userId + " and endTime <= 0 and createTime >= " + startTime + " and createTime < " + endTime + " order by id desc";
		final String hql = "from BreakDownHistory where userId=" + userId + " and endTime = 0 ORDER BY id DESC";
		final Long p = pageNo;
		return dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				query.setFirstResult((p.intValue() - 1) * CommonUtil.pageSize);
				query.setMaxResults(CommonUtil.pageSize);
				return query.list();
			}
		});
	}

	public Long countNotResumeItemsByUserId(Long userId, long startTime, long endTime) {
//		final String hql = "select count(*) from BreakDownHistory where userId=" + userId + " and endTime <= 0 and createTime >= " + startTime + " and createTime < " + endTime;
		final String hql = "select count(*) from BreakDownHistory where userId=" + userId + " and endTime = 0";

		return (Long) dao.getHibernateTemplate().find(hql).iterator().next();
	}

	public Long countBreakDownHistorysByUserIdnCreateTime(Long userId, long startTime, long endTime) {
		final String hql = "select count(*) from BreakDownHistory where userId=" + userId + " and createTime >= " + startTime + " and createTime < " + endTime;
		return (Long) dao.getHibernateTemplate().find(hql).iterator().next();
	}

}
