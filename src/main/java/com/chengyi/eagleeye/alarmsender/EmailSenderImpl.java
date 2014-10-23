package com.chengyi.eagleeye.alarmsender;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

/**
 * 
 * @author wangzhaojun
 * 
 */
public class EmailSenderImpl implements EagleSender {
	public static Logger logger = Logger.getLogger(EmailSenderImpl.class);

	public EmailSenderImpl() {
	}

	private static final EmailSenderImpl instance = new EmailSenderImpl();

	// 163.com
	private static final String mailHost_163 = "smtp.163.com";

	private static final String mailFrom_163 = "ku6sys@163.com"; // systemwatchdog@ku6.com

	private static final String mailFromPassword_163 = "ku6sys1"; // Syswzj,888

//	// ku6.com
//	private static final String mailHost_ku6= "122.11.32.87";
//
//	private static final String mailFrom_ku6 = "ku6sys@ku6.com";
//
//	private static final String mailFromPassword_ku6 = "ku6sys1";
	
	private static final String content = "This is a system email from MonitorSystem, pls do not reply.";

	public boolean send(String mailTo, String mailSubject) {
		SimpleEmail email = new SimpleEmail();
		email.setHostName(mailHost_163);
//		email.setHostName(mailHost_ku6);
//		email.setAuthentication(mailFrom_ku6, mailFromPassword_ku6);
		email.setAuthentication(mailFrom_163, mailFromPassword_163);
		try {
			email.addTo(mailTo);
//			email.setFrom(mailFrom_ku6, "Probe");
			email.setFrom(mailFrom_163, "Probe");
			email.setSubject(mailSubject);
			email.setMsg(content);
			email.setCharset("GBK");

			email.send();
		} catch (EmailException e) {
			e.printStackTrace();
			logger.error(e);
			return false;
		}

		return true;
	}
	
	public boolean send(String mailTo, String mailSubject, String content) {
		HtmlEmail email = new HtmlEmail();
//		email.setHostName(mailHost_ku6);
//		email.setAuthentication(mailFrom_ku6, mailFromPassword_ku6);
		email.setHostName(mailHost_163);
        email.setAuthentication(mailFrom_163, mailFromPassword_163);
		try {
			email.addTo(mailTo);
//			email.setFrom(mailFrom_ku6, "DailyReport");
			email.setFrom(mailFrom_163, "DailyReport");
			email.setSubject(mailSubject);
			email.setMsg(content);
			email.setCharset("GBK");

			email.send();
		} catch (EmailException e) {
			e.printStackTrace();
			logger.error(e);
			return false;
		}

		return true;
	}
	
	public boolean send2(String mailTo, String mailSubject, String content) {
		HtmlEmail email = new HtmlEmail();
		email.setHostName(mailHost_163);
		email.setAuthentication(mailFrom_163, mailFromPassword_163);
		try {
			email.addTo(mailTo);
			email.setFrom(mailFrom_163, "DailyReport");
			email.setSubject(mailSubject);
			email.setMsg(content);
			email.setCharset("GBK");

			email.send();
		} catch (EmailException e) {
			e.printStackTrace();
			logger.error(e);
			return false;
		}

		return true;
	}

	private boolean sendMail(String to, String mailSubject) {
		return send(to, mailSubject);
	}

	public static void main(String[] args) {
		new EmailSenderImpl().sendMail("122.11.23.34 is down", "wangnewton@wo.com.cn");
	}

	public static EagleSender getInstance() {
		return instance;
	}

}
