package com.chengyi.eagleeye.service;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.chengyi.eagleeye.model.Item;
import com.chengyi.eagleeye.patrol.DaoImpl;
import com.chengyi.eagleeye.util.CommonUtil;

/**
 * @author wangzhaojun
 * 
 */
public class ItemMgr {

	@Autowired
	private DaoImpl dao;

	public Item getItem(Long id) {
		return (Item) dao.getHibernateTemplate().get(Item.class, id);
	}

	public Item saveItem(Item item) {
		dao.getHibernateTemplate().save(item);
		return item;
	}

	public Item updateItem(Item item) {
		dao.getHibernateTemplate().update(item);
		return item;
	}
	
	private String getAllServiceTypes() {
		return Item.TYPE_NGINX + ", " + Item.TYPE_RESIN + ", " + Item.TYPE_APACHE + ", " + Item.TYPE_REDIS;
	}
	
	private String getAllHttpTypes() {
		return Item.TYPE_HTTP + ", " + Item.TYPE_PING;
	}
	
	public Long countItemsByUserId(Long userId) {
		return (Long) dao.getHibernateTemplate().find("select count(*) from Item where status!=-1 and userId=" + userId + " and type in (" + Item.TYPE_HTTP + ", " + Item.TYPE_PING + ")").iterator().next();
	}

	public Long countServiceItemsByUserId(Long userId) {
		return (Long) dao.getHibernateTemplate().find("select count(*) from Item where status!=-1 and userId=" + userId + " and type in (" + getAllServiceTypes() + ")").iterator().next();
	}
	
	@SuppressWarnings("unchecked")
	public List<Item> getItemsByUserId(Long userId, Long page) {
		final String hql = "from Item where userId=" + userId + " and type in (" + Item.TYPE_HTTP + ", " + Item.TYPE_PING + ") and status!=-1 order by createtime desc";
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
	
	@SuppressWarnings("unchecked")
	public List<Item> getServiceItemsByUserId(Long userId, Long page) {
		final String hql = "from Item where userId=" + userId + " and type in (" + getAllServiceTypes() + ") and status!=-1 order by createtime desc";
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

	@SuppressWarnings("unchecked")
	public List<Item> getItemsByUserId(Long userId) {
		final String hql = "from Item where userId=" + userId + " and type in (" + Item.TYPE_HTTP + ", " + Item.TYPE_PING + ") and status=1 order by createtime desc";
		return dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<Item> getServiceItemsByUserId(Long userId) {
		final String hql = "from Item where userId=" + userId + " and type in (" + getAllServiceTypes() + ") and status=1 order by createtime desc";
		return dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(hql);
				return query.list();
			}
		});
	}
	
	public Long countWebItemsByGid(Long gid) {
		return (Long) dao.getHibernateTemplate().find("select count(*) from Item where status!=-1 and gid=" + gid + " and type in(" + getAllHttpTypes() + ")").iterator().next();
	}
	
	public Long countServiceItemsByGid(Long gid) {
		return (Long) dao.getHibernateTemplate().find("select count(*) from Item where status!=-1 and gid=" + gid + " and type in (" + getAllServiceTypes() + ")").iterator().next();
	}

	public Long countItemsByTid(Long userId, Integer tid) {
		return (Long) dao.getHibernateTemplate().find("select count(*) from Item where status!=-1 and userId=" + userId + " and type=" + tid).iterator().next();
	}
	
	@SuppressWarnings("unchecked")
	public List<Item> getItemsByGid(Long gid, Long page) {
		final String hql = "from Item where gid=" + gid + " and status!=-1 order by createtime desc";
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

	@SuppressWarnings("unchecked")
	public List<Item> getItemsByTid(Long userId, Integer tid, Long page) {
		final String hql = "from Item where userId= " + userId + " and type=" + tid + " and status!=-1 order by createtime desc";
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
