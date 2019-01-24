package com.lumosity.order;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.core.Controller;
import com.lumosity.login.oauth.AliPayService;
import com.lumosity.model.Order;

public class AlipayNotifyController extends Controller {

	public void index() {
		HttpServletRequest request = this.getRequest();
		String outTradeNo = request.getParameter("out_trade_no");
		
		if (AliPayService.verifyNotify(outTradeNo, request)) {
			Order.dao.successOrder(outTradeNo);
		}
	}
}
