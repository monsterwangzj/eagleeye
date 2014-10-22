package com.chengyi.eagleeye.alarmsender;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.chengyi.eagleeye.network.http.HttpClientUtil;
import com.chengyi.eagleeye.util.ApplicaRuntime;
import com.chengyi.eagleeye.util.MD5;

/**
 * @author wangzhaojun
 * 
 */
public class SmsSenderImpl implements EagleSender {
	private static Logger logger = Logger.getLogger(SmsSenderImpl.class);

	private static String OPEN_SMG_URL = "http://open-hps.sdo.com/";
	private static final String UTF8 = "UTF-8";
	
	private static String merchant_name = "991000294_949";
	private static String client_id = "991000294";
	private static String client_secret = "6b8b4567516cbf240000000007528ba0";
	private static String PID = "200000000607";
	private static String PID2 = "200000001041";
	private static String templateId = "10001452";
	private static String templateId1 = "10002041"; // OK
	private static String templateId2 = "10002042"; // DOWN
	private static String templateId3 = "10002043"; // INSTABLE

	private static String seq = "1065502180299";

	private SmsSenderImpl() {
	}

	private static final SmsSenderImpl instance = new SmsSenderImpl();

	public boolean send(String to, String content) { // logtask is down!!!, stat[122.11.32.63] is OK!
		if (!ApplicaRuntime.isSendSms) return false;
		
		// parse to template
		String text = content.toLowerCase();
		String templateId = null;
		if (text != null) {
			if (text.contains("ok")) {
				templateId = templateId1;
			} else if (text.contains("down")) {
				templateId = templateId2;
			} else if (text.contains("instable")) {
				templateId = templateId3;
			}
		}
		String sec1 = content, sec2 = "";
		Pattern pattern = Pattern.compile(".*(\\[.+\\]).*");
		Matcher mat = pattern.matcher(content);
		if (mat.find()) {
			sec2 = mat.group(1);
			int index = content.indexOf(sec2);
			sec1 = content.substring(0, index);
		} else {
			int index = content.lastIndexOf("is");
			sec1 = content.substring(0, index);
		}
		
//		sec1 = "122.11.[32.119";
//		sec2 = "\\[abc\\]";
		String msg = sec1 + "||" + sec2;
		logger.info("msg: " + msg + ", templateId:" + templateId);
		openNTemplateSubmit(merchant_name, client_id, client_secret, PID2, templateId, seq, to, msg);
		return true;
	}
	
	public boolean send2(String to, String content) { // 122.11.32.119|| is down!!!
		if (!ApplicaRuntime.isSendSms) return false;
		
		openTemplateSubmit(merchant_name, client_id, client_secret, PID, templateId, seq, to, content);
		return true;
	}
	
	/**
	 * 模板下行
	 *
	 * @param clientId
	 * @param key
	 * @param pid
	 * @param templateId
	 * @param seq
	 * @param phone
	 * @param msg
	 * @return
	 */
	private static String openTemplateSubmit(String merchant_name,String clientId, String key, String pid, String templateId, String seq, String phone, String msg) {
	    long timestamp = System.currentTimeMillis() / 1000;
	    
	    StringBuilder md5 = new StringBuilder();
	    md5.append("client_id=").append(clientId)
	            .append("merchant_name=").append(merchant_name)
	            .append("msg=").append(msg)
	            .append("phone=").append(phone)
	            .append("pid=").append(pid)
	            .append("seq=").append(seq)
	            .append("signature_method=MD5")
	            .append("templateId=").append(templateId)
	            .append("timestamp=").append(timestamp)
	            .append(key);
	    
	    StringBuilder url = new StringBuilder();
	    url.append(OPEN_SMG_URL).append("smg/mt/templateSubmit")
	            .append("?seq=").append(seq)
	            .append("&pid=").append(pid)
	            .append("&templateId=").append(templateId)
	            .append("&phone=").append(encode(phone, UTF8))
	            .append("&msg=").append(encode(msg, UTF8))
	            .append("&client_id=").append(clientId)
	            .append("&merchant_name=").append(merchant_name)
	            .append("&signature=").append(MD5.MD5Encode(md5.toString(), UTF8))
	            .append("&signature_method=MD5")
	            .append("&timestamp=").append(timestamp);
	    logger.info("MD5: " + md5.toString());
	    logger.info("URL: " + url.toString());
	    
	    String result = HttpClientUtil.get(url.toString()).getResponseContent();
	    logger.info("result:" + result);
	    return result;
	}

	public static EagleSender getInstance() {
		return instance;
	}
	
	/**
     * 模板下行
     *
     * @param clientId
     * @param key
     * @param pid
     * @param templateId
     * @param seq
     * @param phone
     * @param msg
     * @return
     */
    private static String openNTemplateSubmit(String merchant_name,String clientId, String key, String pid, String templateId, String seq, String phone, String msg) {
        long timestamp = System.currentTimeMillis() / 1000;
        
        StringBuilder md5 = new StringBuilder();
        md5.append("client_id=").append(clientId)
                .append("merchant_name=").append(merchant_name)
                .append("msg=").append(msg)
                .append("phone=").append(phone)
                .append("pid=").append(pid)
                .append("seq=").append(seq)
                .append("signature_method=MD5")
                .append("templateId=").append(templateId)
                .append("timestamp=").append(timestamp)
                .append(key);
        
        StringBuilder url = new StringBuilder();
        url.append(OPEN_SMG_URL).append("smg/mt/templateSubmit")
                .append("?seq=").append(seq)
                .append("&pid=").append(pid)
                .append("&templateId=").append(templateId)
                .append("&phone=").append(encode(phone, UTF8))
                .append("&msg=").append(encode(msg, UTF8))
                .append("&client_id=").append(clientId)
                .append("&merchant_name=").append(merchant_name)
                .append("&signature=").append(MD5.MD5Encode(md5.toString(), UTF8))
                .append("&signature_method=MD5")
                .append("&timestamp=").append(timestamp);
        logger.info("MD5: " + md5.toString());
        logger.info("URL: " + url.toString());
        
        String result = HttpClientUtil.get(url.toString()).getResponseContent();
        logger.info("result:" + result);
        return result;
    }
       
	private static String encode(String source, String enc) {
		try {
			if (StringUtils.isNotBlank(source)) {
				String result = URLEncoder.encode(source, enc);
				result = result.replaceAll("\\+", "%20");
				logger.info(result);
				return result;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) {
		System.out.println(encode("测试200 is DOWN!!!", UTF8));
	}
}
