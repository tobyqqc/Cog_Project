package com.lumosity.settings.weixin;

import com.jfinal.core.Controller;
import com.lumosity.model.Account;
import com.lumosity.model.OauthUser;

public class WeixiPcUnbindController extends Controller {

	public void index() {
		Account account = getSessionAttr("userInfo");
		String openId = getPara("openId");
		OauthUser.dao.unbind(openId, account.getUserId());
		setSessionAttr("WXUser",null);
		redirect("/settings");
	}
}
