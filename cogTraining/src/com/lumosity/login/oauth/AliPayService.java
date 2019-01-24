package com.lumosity.login.oauth;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.config.AlipayConfig;
import com.lumosity.login.oauth.meta.ShareLoginDict;
import com.lumosity.utils.DateTimeKit;

public class AliPayService {
	
	/**
	 * 生成请求参数
	 * @param sn
	 * @param amount
	 * @param description
	 * @param request
	 * @return
	 */
    public static Map<String, String> getParameterMap(String sn, BigDecimal amount, String description, HttpServletRequest request) {
        Map<String, String> parameterMap = new HashMap<>(18);
        parameterMap.put("app_id", ShareLoginDict.ALIPAY_APPID);
        // biz_content
        Map<String, String> contentParameterMap = new HashMap<>(16);
        contentParameterMap.put("body", description);
        contentParameterMap.put("subject", description);
        contentParameterMap.put("timeout_express", "150m");
        contentParameterMap.put("out_trade_no", sn);
        contentParameterMap.put("body", description);
        contentParameterMap.put("total_amount", "" + amount.doubleValue());
        contentParameterMap.put("product_code", "QUICK_MSECURITY_PAY");
        parameterMap.put("biz_content",  JSONObject.toJSONStringWithDateFormat(contentParameterMap, "yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat));

        parameterMap.put("charset", "utf-8");
        parameterMap.put("method", "alipay.trade.app.pay");
        parameterMap.put("notify_url", getNotifyUrl());
        parameterMap.put("sign_type", "RSA2");
        parameterMap.put("timestamp", DateTimeKit.getPlayDate2());
        parameterMap.put("version", "1.0");
        parameterMap.put("sign", generateSign(parameterMap));
        return parameterMap;
    }

    /**
     * 验证签名
     * @param sn
     * @param request
     * @return
     */
    public static boolean verifyNotify(String sn, HttpServletRequest request) {
        Map<String, String[]> requestParameterMap = request.getParameterMap();
        Map<String, String> parameterMap = new HashMap<>(requestParameterMap.size());
        for (Map.Entry<String, String[]> entry : requestParameterMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue()[0]);
            parameterMap.put(entry.getKey(), entry.getValue()[0]);
        }
        String appId = parameterMap.get("app_id");
        if (!ShareLoginDict.ALIPAY_APPID.equals(appId)) {
            return false;
        }
        try {
            return AlipaySignature.rsaCheckV1(parameterMap, ShareLoginDict.ALIPAY_PUBLIC_KEY, "UTF-8", "RSA2");
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    protected static String getNotifyUrl() {
		return ShareLoginDict.ALIPAY_NOTIFY_URL;
	}
    
    /**
     * 生成签名
     *
     * @param parameterMap 参数
     * @return 签名
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static String generateSign(Map<String, String> parameterMap) {
        SortedMap<String, String> sortedMap = new TreeMap();
        sortedMap.putAll(parameterMap);
        List<String> keys = new ArrayList(parameterMap.keySet());
        Collections.sort(keys);
        StringBuffer toSign = new StringBuffer();
        Iterator i$ = keys.iterator();
        while (i$.hasNext()) {
            String key = (String) i$.next();
            String value = parameterMap.get(key);
            if (null != value && !"".equals(value) && !"sign".equals(key) && !"key".equals(key)) {
                toSign.append(key + "=" + value + "&");
            }
        }
        // 第一步原始字符串
        String content = toSign.substring(0, toSign.length() - 1);
        String sign;
        try {
            // 第二部，生成签名
            sign = AlipaySignature.rsa256Sign(content, ShareLoginDict.ALIPAY_PRIVATE_KEY, "UTF-8");
            System.out.println("2:" + sign);
            // 第三部，签名链接在原值字符串后
            content += ("&sign=" + URLEncoder.encode(sign, "UTF-8"));
        } catch (AlipayApiException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return content;
    }
}
