package com.lumosity.order;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.core.Controller;
import com.lumosity.login.oauth.WxService;
import com.lumosity.model.Order;
import com.lumosity.utils.WeixinUtils;

/**
 * 微信支付回调
 * @author yesong
 *
 */
public class WxNotiyfyController extends Controller {
	
	
	public void index() {
		HttpServletRequest request = this.getRequest();
		Map<String, String> newParams = new HashMap<>(request.getParameterMap().size());
		// 返回结果
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line;
			String xmlData = "";
			while ((line = reader.readLine()) != null) {
				xmlData += line;
			}
			System.out.println("result:" + xmlData);
			newParams = WeixinUtils.doXMLParse(xmlData);
			boolean result = WxService.verifyNotify(newParams.get("out_trade_no"), newParams);
			if (result) {
				Order.dao.successOrder(newParams.get("out_trade_no"));
			}
			redirect("/home");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
