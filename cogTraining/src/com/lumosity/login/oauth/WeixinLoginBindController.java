package com.lumosity.login.oauth;

import java.util.Date;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.lumosity.model.Account;
import com.lumosity.model.OauthUser;

public class WeixinLoginBindController extends Controller {
	
	public void index() {
		String openId = this.getPara("openId");
    	String mobile = this.getPara("accountInfo");
    	String userName = this.getPara("userName");
    	String code = this.getPara("code");
    	String password = this.getPara("password");
    	
		if (!code.equals(CacheKit.get("valiCode", mobile))) {
			setAttr("msg", "验证码错误");
			setAttr("nickname", userName);
			setAttr("openId", openId);
			render("/bind.html");
			return;
		}
        try {
        	Date birth = this.getParaToDate("date");
        	Account account = Account.dao.findPwd(mobile);
            boolean isExit = (account!= null && account.get("userId") != null);
            if (isExit) {
            	password = account.getPassword();
            }
         	Map<String, Object> returnInfo = OauthUser.dao.bindMobile(openId, userName, birth, mobile, code, password);
        	Account account2 = Account.dao.findById(returnInfo.get("userId"));
        	System.out.println("bind.account=" + account2);
        	setSessionAttr("userInfo", account2);
        	redirect("/home");
        } catch (Exception e) {
            e.printStackTrace();
//            return ApiResult.failure();
            renderJson("msg", "系统错误");
        }
	}
}
