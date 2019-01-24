package com.lumosity.settings.weixin;

import com.jfinal.core.Controller;
import com.lumosity.login.oauth.WxService;
import com.lumosity.login.oauth.meta.ShareLoginDict;
import com.lumosity.model.Account;

public class WeixinPCLoginController extends Controller {

	public void index() {
		Account account = getSessionAttr("userInfo");
		System.out.println("acount:" + account);
		redirect(WxService.oauth2buildQrconnectUrl(ShareLoginDict.weixinPCLoginNotify + "?userId=" + account.getUserId(),
				ShareLoginDict.weiScope, ShareLoginDict.weixinState));
	}
}
