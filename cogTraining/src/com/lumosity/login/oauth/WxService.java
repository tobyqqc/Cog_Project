package com.lumosity.login.oauth;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.Consts;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.lumosity.login.oauth.meta.ShareLoginDict;
import com.lumosity.order.MyWxMpPrepayIdResult;
import com.lumosity.utils.URIUtil;
import com.thoughtworks.xstream.XStream;

import me.chanjar.weixin.common.bean.result.WxError;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.crypto.SHA1;
import me.chanjar.weixin.common.util.crypto.WxCryptUtil;
import me.chanjar.weixin.common.util.http.RequestExecutor;
import me.chanjar.weixin.common.util.http.SimpleGetRequestExecutor;
import me.chanjar.weixin.common.util.http.Utf8ResponseHandler;
import me.chanjar.weixin.common.util.xml.XStreamInitializer;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpPrepayIdResult;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

public class WxService {

	/**
	 * 微信开放平台
	 */
	public static final String WEIXIN_OPEN = "WEIXINOPEN";
	/**
	 * 微信公众平台
	 */
	public static final String WEIXIN_MP = "WEIXINMP";

	public static boolean checkSignature(String timestamp, String nonce, String signature) {
		try {
			return SHA1.gen(ShareLoginDict.weixinMpToken, timestamp, nonce).equals(signature);
		} catch (Exception e) {
			return false;
		}
	}

	public static String oauth2buildAuthorizationUrl(String redirectURI, String scope, String state) {
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?";
		url += "appid=" + ShareLoginDict.weixinMpAppid;
		url += "&redirect_uri=" + URIUtil.encodeURIComponent(redirectURI);
		url += "&response_type=code";
		url += "&scope=" + scope;
		if (state != null) {
			url += "&state=" + state;
		}
		url += "#wechat_redirect";
		System.out.println("oauth2buildAuthorizationUrl:" + url);
		return url;
	}

	public static String oauth2buildQrconnectUrl(String redirectURI, String scope, String state) {
		String url = "https://open.weixin.qq.com/connect/qrconnect?";
		url += "appid=" + ShareLoginDict.weixinPCAppid;
		url += "&redirect_uri=" + URIUtil.encodeURIComponent(redirectURI);
		url += "&response_type=code";
		url += "&scope=" + scope;
		if (state != null) {
			url += "&state=" + state;
		}
		url += "#wechat_redirect";
		return url;
	}

	public static WxMpOAuth2AccessToken oauth2getAccessToken(String code) throws WxErrorException {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?";
		url += "appid=" + ShareLoginDict.weixinPCAppid;
		url += "&secret=" + ShareLoginDict.WeixinPCAppSecret;
		url += "&code=" + code;
		url += "&grant_type=authorization_code";

		try {
			RequestExecutor<String, String> executor = new SimpleGetRequestExecutor();
			String responseText = executor.execute(getHttpclient(), null, url, null);
			return WxMpOAuth2AccessToken.fromJson(responseText);
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static WxMpOAuth2AccessToken oauth2getMpAccessToken(String code) throws WxErrorException {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?";
		url += "appid=" + ShareLoginDict.weixinMpAppid;
		url += "&secret=" + ShareLoginDict.WeixinMpAppSecret;
		url += "&code=" + code;
		url += "&grant_type=authorization_code";

		try {
			RequestExecutor<String, String> executor = new SimpleGetRequestExecutor();
			String responseText = executor.execute(getHttpclient(), null, url, null);
			return WxMpOAuth2AccessToken.fromJson(responseText);
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

//	public WxMpUser userInfo(String openid, String lang) throws WxErrorException {
//		String url = "https://api.weixin.qq.com/cgi-bin/user/info";
//		lang = lang == null ? "zh_CN" : lang;
//		String responseContent = execute(new SimpleGetRequestExecutor(), url, "openid=" + openid + "&lang=" + lang);
//		return WxMpUser.fromJson(responseContent);
//	}

	public static WxMpUser oauth2getUserInfo(WxMpOAuth2AccessToken oAuth2AccessToken, String lang)
			throws WxErrorException {
		String url = "https://api.weixin.qq.com/sns/userinfo?";
		url += "access_token=" + oAuth2AccessToken.getAccessToken();
		url += "&openid=" + oAuth2AccessToken.getOpenId();
		if (lang == null) {
			url += "&lang=zh_CN";
		} else {
			url += "&lang=" + lang;
		}

		try {
			RequestExecutor<String, String> executor = new SimpleGetRequestExecutor();
			String responseText = executor.execute(getHttpclient(), null, url, null);
			return WxMpUser.fromJson(responseText);
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<String, String> getParameterMap(String sn, BigDecimal amount, String description,
			HttpServletRequest request, String platform) {
		WxMpPrepayIdResult wxMpPrepayIdResult = getPrepayId("", sn, amount.doubleValue(), description, "APP",
				request.getRemoteAddr(), getNotifyUrl(sn, "wxpay"), platform);
		String prepayId = wxMpPrepayIdResult.getPrepay_id();
		if (prepayId == null || prepayId.equals("")) {
			System.out.println("return_code:" + wxMpPrepayIdResult.getReturn_code());
			System.out.println("return_msg:" + wxMpPrepayIdResult.getReturn_msg());
			// throw new RuntimeException("get prepayid error");
		}
		Map<String, String> payInfo = new HashMap<>(8);
		payInfo.put("appid", wxMpPrepayIdResult.getAppid());
		payInfo.put("noncestr", System.currentTimeMillis() + "");
		payInfo.put("package", "Sign=WXPay");
		payInfo.put("partnerid", wxMpPrepayIdResult.getMch_id());
		payInfo.put("prepayid", wxMpPrepayIdResult.getPrepay_id());
		// 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
		payInfo.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
		String sign = WxCryptUtil.createSign(payInfo, ShareLoginDict.WEIXIN_PARTNERKEY);
		payInfo.put("sign", sign);
		return payInfo;
	}

	protected static String getNotifyUrl(String sn, String type) {
		return ShareLoginDict.weixinPayNotify;
	}

	public static MyWxMpPrepayIdResult getPrepayId(String openId, String outTradeNo, double amt, String body,
			String tradeType, String ip, String callbackUrl, String platform) {
		String nonce_str = System.currentTimeMillis() + "";

		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid",
				WEIXIN_OPEN.equals(platform) ? ShareLoginDict.weixinAppid : ShareLoginDict.weixinMpAppid);
		packageParams.put("mch_id", ShareLoginDict.WEIXIN_PARTNERID);
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("body", body);
		packageParams.put("out_trade_no", outTradeNo);

		packageParams.put("total_fee", (int) (amt * 100) + "");
		packageParams.put("spbill_create_ip", ip);
		packageParams.put("notify_url", callbackUrl);
		packageParams.put("trade_type", tradeType);
		packageParams.put("openid", openId);

		String sign = WxCryptUtil.createSign(packageParams, ShareLoginDict.WEIXIN_PARTNERKEY);
		String xml = "<xml>" + "<appid>"
				+ (WEIXIN_OPEN.equals(platform) ? ShareLoginDict.weixinAppid : ShareLoginDict.weixinMpAppid)
				+ "</appid>" + "<mch_id>" + ShareLoginDict.WEIXIN_PARTNERID + "</mch_id>" + "<nonce_str>" + nonce_str
				+ "</nonce_str>" + "<sign>" + sign + "</sign>" + "<body><![CDATA[" + body + "]]></body>"
				+ "<out_trade_no>" + outTradeNo + "</out_trade_no>" + "<total_fee>" + packageParams.get("total_fee")
				+ "</total_fee>" + "<spbill_create_ip>" + ip + "</spbill_create_ip>" + "<notify_url>" + callbackUrl
				+ "</notify_url>" + "<trade_type>" + tradeType + "</trade_type>" + "<openid>" + openId + "</openid>"
				+ "</xml>";

		HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/pay/unifiedorder");
		StringEntity entity = new StringEntity(xml, Consts.UTF_8);
		httpPost.setEntity(entity);
		try {
			CloseableHttpResponse response = getHttpclient().execute(httpPost);
			String responseContent = Utf8ResponseHandler.INSTANCE.handleResponse(response);
			System.out.println("responseContent:" + responseContent);
			XStream xstream = XStreamInitializer.getInstance();
			xstream.alias("xml", MyWxMpPrepayIdResult.class);
			MyWxMpPrepayIdResult wxMpPrepayIdResult = (MyWxMpPrepayIdResult) xstream.fromXML(responseContent);
			return wxMpPrepayIdResult;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new MyWxMpPrepayIdResult();
	}

	/**
	 * 公众号网页支付
	 * 
	 * @param openId
	 * @param outTradeNo
	 * @param amt
	 * @param body
	 * @param tradeType
	 * @param ip
	 * @param callbackUrl
	 * @return
	 */
	public static Map<String, String> getJSSDKPayInfo(String openId, String outTradeNo, double amt, String body,
			String tradeType, String ip, String callbackUrl) {
		MyWxMpPrepayIdResult wxMpPrepayIdResult = getPrepayId(openId, outTradeNo, amt, body, tradeType, ip, callbackUrl,
				WEIXIN_MP);
		String prepayId = wxMpPrepayIdResult.getPrepay_id();
		if (prepayId == null || prepayId.equals("")) {
			System.out.println("return_code:" + wxMpPrepayIdResult.getReturn_code());
			System.out.println("return_msg:" + wxMpPrepayIdResult.getReturn_msg());
			throw new RuntimeException("get prepayid error");
		}
		Map<String, String> payInfo = new HashMap<String, String>();
		payInfo.put("appId", ShareLoginDict.weixinMpAppid);
		// 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
		payInfo.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
		payInfo.put("nonceStr", System.currentTimeMillis() + "");
		payInfo.put("package", "prepay_id=" + prepayId);
		payInfo.put("signType", "MD5");

		String finalSign = WxCryptUtil.createSign(payInfo, ShareLoginDict.WEIXIN_PARTNERKEY);
		payInfo.put("paySign", finalSign);
		return payInfo;
	}

	/**
	 * 扫码支付
	 * 
	 * @param openId
	 * @param outTradeNo
	 * @param amt
	 * @param body
	 * @param tradeType
	 * @param ip
	 * @param callbackUrl
	 * @return
	 */
	public static String getNativeSDKPayInfo(String openId, String outTradeNo, double amt, String body,
			String tradeType, String ip, String callbackUrl) {
		MyWxMpPrepayIdResult wxMpPrepayIdResult = getPrepayId(openId, outTradeNo, amt, body, tradeType, ip, callbackUrl,
				WEIXIN_OPEN);
		String prepayId = wxMpPrepayIdResult.getPrepay_id();
		if (prepayId == null || prepayId.equals("")) {
			System.out.println("return_code:" + wxMpPrepayIdResult.getReturn_code());
			System.out.println("return_msg:" + wxMpPrepayIdResult.getReturn_msg());
			throw new RuntimeException("get prepayid error");
		}
		System.out.println("code_url:" + wxMpPrepayIdResult.getCode_url());
		// code_url:weixin://wxpay/bizpayurl?pr=ZuxBTu1
		return wxMpPrepayIdResult.getCode_url();
	}

	public static boolean verifyNotify(String sn, Map<String, String> newParams) {
		return newParams.get("appid").equals(ShareLoginDict.weixinAppid)
				&& "SUCCESS".equals(newParams.get("return_code"))
				&& WeixinSignature.rsaCheckV1(newParams, ShareLoginDict.WEIXIN_PARTNERKEY);
	}

	protected static CloseableHttpClient getHttpclient() {
		return HttpClients.createDefault();
	}
}
