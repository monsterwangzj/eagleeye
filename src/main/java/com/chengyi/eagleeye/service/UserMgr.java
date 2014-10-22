package com.chengyi.eagleeye.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.chengyi.eagleeye.model.User;
import com.chengyi.eagleeye.patrol.DaoImpl;

/**
 * @author wangzhaojun
 * 
 */
@SuppressWarnings("unchecked")
public class UserMgr {
	private static Logger logger = Logger.getLogger(UserMgr.class);

	@Autowired
	private DaoImpl dao;

	public User saveUser(User user) {
		dao.getHibernateTemplate().save(user);
		return user;
	}

	public User getUser(String username, String password) {
		String hql = "from User where username='" + username + "' and password='" + password + "'";
		List<User> userList = dao.getHibernateTemplate().find(hql);
		if (CollectionUtils.isNotEmpty(userList)) {
			return userList.get(0);
		} else {
			return null;
		}
	}

	public User getUser(Long id) {
		return (User) dao.getHibernateTemplate().get(User.class, id);
	}

	public User updateUser(User user) {
		dao.getHibernateTemplate().update(user);
		return user;
	}
	
}
