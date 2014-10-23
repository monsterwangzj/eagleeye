package eagleeye;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.Test;

public class Test1 {

    @Test
    public void test11() throws UnsupportedEncodingException {
        String param = "operationType=alipay.secuprod.appconfig.globalConfig&requestData=[{\"mainVersion\":\"1.0.001\"}]";
        String demo = "http://mobilegw.d5795aqcn.alipay.net/mgw.htm?";
        
        String encode = URLEncoder.encode(param, "UTF-8");
        System.out.println(encode);
        System.out.println(demo + encode);
    }

}
