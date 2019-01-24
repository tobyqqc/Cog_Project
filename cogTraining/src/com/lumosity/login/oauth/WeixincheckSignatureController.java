package com.lumosity.login.oauth;

import com.jfinal.core.Controller;

public class WeixincheckSignatureController extends Controller {

	public void index() {
		String signature = this.getPara("signature");
		String timestamp = this.getPara("timestamp");
		String nonce = this.getPara("nonce");
		String echostr = this.getPara("echostr");
		if (WxService.checkSignature(timestamp, nonce, signature)) {
			renderJson(echostr);
		}
		else {
			renderJson("error");
		}
	}
}
