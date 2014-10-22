package com.chengyi.eagleeye.service;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.chengyi.eagleeye.model.AlarmHistory;
import com.chengyi.eagleeye.patrol.DaoImpl;
import com.chengyi.eagleeye.util.CommonUtil;

/**
 * @author wangzhaojun
 * 
 */
public class AlarmHistoryMgr {
	private static Logger logger = Logger.getLogger(AlarmHistoryMgr.class);

	@Autowired
	private DaoImpl dao;

	public AlarmHistory saveAlarmHistory(AlarmHistory alarmHistory) {
		dao.getHibernateTemplate().save(alarmHistory);
		return alarmHistory;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Byte, Integer> countAlarmsByDatenChannel(long startTime, long endTime) {
		final String sql = "select alarmchannel, count(*) from alarmhistory where createtime >= " + startTime + " and createtime <" + endTime + " group by alarmchannel";
		List<Object[]> list = dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query q = session.createSQLQuery(sql);
				return q.list();
			}
		});
		Map<Byte, Integer> countMap = new TreeMap<Byte, Integer>();
		for (Object[] datas : list) {
			System.out.println(datas[0] + "\t" + datas[1]);
			countMap.put(Byte.parseByte(datas[0].toString()), Integer.parseInt(datas[1].toString()));
		}
		return countMap;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Byte, Integer> countAlarmsByDatenChannelnUserId(long startTime, long endTime, Long userId) {
		final String sql = "select alarmchannel, count(*) from alarmhistory where userid=" + userId + " and createtime >= " + startTime + " and createtime <" + endTime + " group by alarmchannel";
		List<Object[]> list = dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query q = session.createSQLQuery(sql);
				return q.list();
			}
		});
		Map<Byte, Integer> countMap = new TreeMap<Byte, Integer>();
		for (Object[] datas : list) {
			System.out.println(datas[0] + "\t" + datas[1]);
			countMap.put(Byte.parseByte(datas[0].toString()), Integer.parseInt(datas[1].toString()));
		}
		return countMap;
	}
	
	@SuppressWarnings("unchecked")
	public List<AlarmHistory> getAlarmHistorysByUserIdnCreateTime(Long userId, long startTime, long endTime, Long pageNo) {
		final String hql = "from AlarmHistory where userId=" + userId + " and createTime >= " + startTime + " and createTime < " + endTime + " order by id desc ";
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
	public List<AlarmHistory> getAlarmHistorysByUserIdnCreateTime2(Long userId, long startTime, long endTime, Long pageNo) {
		String sql = "SELECT a.id,a.userid,a.itemid,a.serverip,a.breakdownid,a.alarmchannel,a.errortype,a.monitorid,a.monitoraddr,a.content,a.eventseqid,a.createtime ";
		sql += " FROM `alarmhistory` a INNER JOIN (SELECT max(id) AS id FROM alarmhistory GROUP BY eventseqid) b ON a.id=b.id";
		sql += " WHERE userid=" + userId + " AND a.createtime>=" + startTime + " AND a.createtime<" + endTime +" ORDER BY a.id DESC";
		
		long start = (pageNo - 1) * CommonUtil.pageSize;
		sql += " LIMIT " + start + ", " + CommonUtil.pageSize;
		final String fsql = sql;
		List<Object[]> list = dao.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query q = session.createSQLQuery(fsql);
				return q.list();
			}
		});
		
		List<AlarmHistory> historys = new ArrayList<AlarmHistory>();
		if (list != null && list.size() > 0) {
			for (Object[] obj : list) {
				AlarmHistory history = new AlarmHistory();
				history.setId(new Long((Integer) obj[0]));
				history.setUserId(new Long((Integer) obj[1]));
				history.setItemId(new Long((Integer) obj[2]));
				history.setServerIp((String) obj[3]);
				history.setBreakDownId(obj[4] == null ? 0L : (new Long((Integer) obj[4])));
				
				history.setAlarmChannel((Byte) obj[5]);
				history.setErrorType((Integer) obj[6]);
				history.setMonitorId(new Long((Integer) obj[7]));
				history.setMonitorAddr((String) obj[8]);
				history.setContent((String) obj[9]);
				
				history.setEventSeqId(new Long((Integer) obj[10]));
				history.setCreateTime(((BigInteger)obj[11]).longValue());
				
				historys.add(history);
			}
		}
		return historys;
	}
	
	public Long countAlarmHistorysByUserIdnCreateTime(Long userId, long startTime, long endTime) {
		final String hql = "select count(*) from AlarmHistory where userId=" + userId + " and createTime >= " + startTime + " and createTime < " + endTime;
		return (Long) dao.getHibernateTemplate().find(hql).iterator().next();
	}
	
	@SuppressWarnings("unchecked")
	public Long countAlarmHistorysByUserIdnCreateTime2(Long userId, long startTime, long endTime) {
		String sql = "SELECT count(*) FROM `alarmhistory` INNER JOIN (SELECT max(id) AS id FROM alarmhistory GROUP BY eventseqid) b ON alarmhistory.id=b.id";
		sql += " WHERE alarmhistory.userid=" + userId + " AND alarmhistory.createtime>=" + startTime + " AND alarmhistory.createtime<" + endTime +" ORDER BY alarmhistory.id DESC";
		final String fsql = sql;
		List<BigInteger> list = dao.getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query q = session.createSQLQuery(fsql);
				return q.list();
			}
		});
		if (list != null && list.size() > 0) {
			return list.get(0).longValue();
		} else {
			return 0L;
		}
	}
	
}
