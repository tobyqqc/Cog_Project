package com.lumosity.login.oauth;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.lumosity.utils.WeixinUtils;

/**
 * 微信
 * @author yesong
 * @date 2018/1/5
 */
public class WeixinSignature {
    public static boolean rsaCheckV1(Map<String, String> params, String partnerKey) {
        String sign = params.get("sign");
        String content = getSignCheckContentV1(params);
        return checkContent(content,sign, partnerKey);
    }

    @SuppressWarnings("unchecked")
    public static String getSignCheckContentV1(Map<String, String> params) {
        if(params == null) {
            return null;
        } else {
            params.remove("sign");
            StringBuilder content = new StringBuilder();
            List<String> keys = new ArrayList(params.keySet());
            Collections.sort(keys);

            for(int i = 0; i < keys.size(); ++i) {
                String key = keys.get(i);
                String value = params.get(key);
                content.append((i == 0?"":"&") + key + "=" + value);
            }
            return content.toString();
        }
    }

    public static boolean checkContent(String content, String sign, String partnerKey) {
        content = content + "&key=" + partnerKey;
        String nowSign = WeixinUtils.md5(content).toUpperCase();
        System.out.println("weixin sign:" + sign);
        System.out.println("nowSign:" + nowSign);
        return sign.equals(nowSign);
    }

    public static void main(String[] args) {
//        String xmlDate = "<xml><appid><![CDATA[wx71210d6e33ab50f3]]></appid><bank_type><![CDATA[CFT]]></bank_type><cash_fee><![CDATA[1]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1494664892]]></mch_id><nonce_str><![CDATA[1515130042655]]></nonce_str><openid><![CDATA[om1oI0xxUALGfLMMYohZ41JUt3-c]]></openid><out_trade_no><![CDATA[P20180105132722599191]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[29E565FCDC755B1A765D402964111934]]></sign><time_end><![CDATA[20180105132754]]></time_end><total_fee>1</total_fee><trade_type><![CDATA[APP]]></trade_type><transaction_id><![CDATA[4200000047201801058498428644]]></transaction_id></xml>";
//        try {
//            Map<String, String> paramas = WeixinUtils.doXMLParse(xmlDate);
//            String key = "bb9aae374e667b0bb69011308fe0be29";
//            System.out.println(rsaCheckV1(paramas, key));
//        } catch (JDOMException | IOException e) {
//            e.printStackTrace();
//        }

//        Map<String, String> params = new HashMap<>();
//        params.put("gmt_create", "2018-01-05 13:53:42");
//        params.put("charset", "utf-8");
//        params.put("seller_email", "1518358430@qq.com");
//        params.put("subject", "购买项目");
//        params.put("sign", "COUVdLMGt0VQXm4TNHFbQPEi2Z/LwucocWz4FDJQ8b2g2aF3DZXli9Ajn0oF0GtD4KwxymiZjlP2HimdrNPI4MTSndYwJPZ4RLCRhBGRP14ggE+lmjR7JpehydWOvgXmommkLodmST8zh3Cl8zTTJG3Ocx/h7FZ4PDfh7XNhwqebMy/lnh9P+dg0XU+1ZscBNUThQrd1e3m6rc5t8zDaMaJGURpWghVITCrlKGTOecdrHiKI/wfwI2AQIF7E/UQ/D/79HiCUbvpfaXfyDvOPlXgvaPM82aqlqkwRNaCL59tOWmt82pti8952AhZXD6jZRVSY/8Dxw0Ou8Dsz5qd4EQ==");
//        params.put("body", "购买项目");
//        params.put("buyer_id", "2088712827350184");
//        params.put("invoice_amount", "0.01");
//        params.put("notify_id", "536fcbc25424e1135ce6e5d50e4f7dche5");
//        params.put("fund_bill_list", "[{\"amount\":\"0.01\",\"fundChannel\":\"ALIPAYACCOUNT\"}]");
//        params.put("notify_type", "trade_status_sync");
//        params.put("trade_status", "TRADE_SUCCESS");
//        params.put("receipt_amount", "0.01");
//        params.put("app_id", "2018010401569056");
//        params.put("buyer_pay_amount", "0.01");
//        params.put("sign_type", "RSA2");
//        params.put("seller_id", "2088721851047867");
//        params.put("gmt_payment", "2018-01-05 13:53:43");
//        params.put("notify_time", "2018-01-05 13:53:43");
//        params.put("version", "1.0");
//        params.put("out_trade_no", "P20180105135313885785");
//        params.put("total_amount", "0.01");
//        params.put("trade_no", "2018010521001004180540478437");
//        params.put("auth_app_id", "2018010401569056");
//        params.put("buyer_logon_id", "134****7753");
//        params.put("point_amount", "0.00");
//
//        String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu8XRP/YkWaQih4UB8rO3yZRRTrLM7FTQS6Xwfjo4YXecOjhsGtHhWzyoxO0iaCqb4QruzOPtDMHRqCn9pQ+H8pOJgBcKDcb7guljSNwOgHlA6rrtIEkxGmHkUvoek0SMLIwx4R+rJJ6la68ODrkeJzbdHwaZEgZ3KuITvNimV5sIxS9VrJhnIdmfR1+bieblT0qiQItZnuKqJQb8JRyI5WvaMYkgpKsW/30x5pPT+KJYXK0OkRbRnvg+gJlH+8XlolwAzFDM2E06WGkTaqYryoR2QAwVkVbYmIVgw7w6YcWR1CLKxLp2Z4Rn3iaD/G6u5Zrfch2BbcaJQIKkGZlm9wIDAQAB";
//        try {
//            System.out.println(AlipaySignature.rsaCheckV1(params, key, "UTF-8", "RSA2"));
//        } catch (AlipayApiException e) {
//            e.printStackTrace();
//        }
    }
}
