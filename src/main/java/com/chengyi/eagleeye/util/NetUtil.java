package com.chengyi.eagleeye.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author wangzhaojun01
 */
public class NetUtil {

    public static String getLocalIp() {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress().toString();
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }
        return ip;
    }

}
