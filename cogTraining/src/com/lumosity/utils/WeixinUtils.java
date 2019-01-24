package com.lumosity.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * MD5加密
 * @author yesong
 * @date 2018/1/5
 */
public class WeixinUtils {
    /**
     * 定义加密方式
     * MAC算法可选以下多种算法
     * <pre>
     * HmacMD5
     * HmacSHA1
     * HmacSHA256
     * HmacSHA384
     * HmacSHA512
     * </pre>
     */
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String HmacSHA256 = "HmacSHA256";

    /**
     * 利用MD5进行加密
     * 　　* @param str  待加密的字符串
     * 　　* @return  加密后的字符串
     * 　　* @throws NoSuchAlgorithmException  没有这种产生消息摘要的算法
     * 　　 * @throws UnsupportedEncodingException
     *
     */
    /**
     * 生成md5
     *
     * @param message
     * @return
     */
    public static String md5(String message) {
        String md5str = "";
        try {
            // 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 2 将消息变成byte数组
            byte[] input = message.getBytes();
            // 3 计算后获得字节数组,这就是那128位了
            byte[] buff = md.digest(input);
            // 4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            md5str = bytesToHex(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }

    /**
     * 二进制转十六进制
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        // 把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];
            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }



    /**
     * 生成签名数据_HmacSHA1加密
     *
     * @param data
     *            待加密的数据
     * @param key
     *            加密使用的key
     * @throws NoSuchAlgorithmException
     */
    public static String hmac(String data, String key, String hmac) throws Exception {
        byte[] keyBytes = key.getBytes();
        // 根据给定的字节数组构造一个密钥。
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, hmac);
        Mac mac = Mac.getInstance(hmac);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data.getBytes());
        return byte2hex(rawHmac);
    }

    private static String byte2hex(final byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式。
            stmp = (java.lang.Integer.toHexString(b[n] & 0xFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }

    /**
     * @param strxml
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static Map<String, String> doXMLParse(String strxml) throws JDOMException, IOException {
        strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");
        if(null == strxml || "".equals(strxml)) {
            return null;
        }
        Map<String, String> paramsMaps = new HashMap<>();
        InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List list = root.getChildren();
        Iterator it = list.iterator();
        while(it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.getChildren();
            if(children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = getChildrenText(children);
            }
            paramsMaps.put(k, v);
        }
        in.close();
        return paramsMaps;
    }

    /**
     * ��ȡ�ӽ���xml
     * @param children
     * @return String
     */
    public static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if(!children.isEmpty()) {
            Iterator it = children.iterator();
            while(it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if(!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }

     /**
     * @param args
     */
    public static void main(String[] args) {
//        try {
////            System.out.println(hmac("abc", "abc", HmacSHA256).toUpperCase());
//            System.out.println(md5("123456"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");
        list.add("8");
        list.add("9");
        int index = 0;
        int step = 6;
        int times = list.size() / step;
        int timesMod = list.size() % step;
        if (timesMod != 0) {
        	times += 1;
        }
        
        for (int i = 1; i <= times; i++) {
        	int step2 = ((step * i) > list.size()) ?  list.size() : (step * i);
        	List<String> subList = list.subList(index, step2);
        	System.out.println("times:" + i);
        	for (String str : subList) {
        		System.out.println("str：" + str);
        	}
        	index = (index + step);
        }
    }
}
 