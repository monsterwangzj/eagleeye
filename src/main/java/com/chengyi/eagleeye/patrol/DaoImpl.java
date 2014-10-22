package com.chengyi.eagleeye.patrol;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class DaoImpl extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Object> queryList(String hql, Object[] params) {
		return getHibernateTemplate().find(hql, params);
	}

}
