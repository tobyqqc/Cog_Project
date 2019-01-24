package com.lumosity.login.oauth;


import org.apache.commons.lang3.StringUtils;

import com.jfinal.core.Controller;
import com.lumosity.model.Account;
import com.lumosity.model.OauthUser;


public class QQLoginNotifyController extends Controller {

	public void index() {
		String userId = this.getPara("userId");
		if (StringUtils.isBlank(userId)) {
			render("/login.html");
		}
		else {
			setAttr("userId", userId);
			render("/qqlogin_success.html");
		}
	}
	
	public void login() {
		String openId = this.getPara("openId");
		String nickname = this.getPara("nickname");
		String genderName = this.getPara("gender");
		System.out.println("openId:" +openId + "=nickname:" + nickname +"=:genderName" +genderName);
		try {
	    	String cover = "";
	    	Integer platform = 1;
	    	Integer gender = (genderName.equals("男") ? 0 : 1);
	    	
	        OauthUser oauthUser = new OauthUser();
	        oauthUser.setOpenId(openId);
	        oauthUser.setCover(cover);
	        oauthUser.setNickname(nickname);
	        oauthUser.setPlatform(platform);
	        oauthUser.setGender(gender);
	        try {
	            boolean flag = OauthUser.dao.oauthLogin(oauthUser);
	            if (flag) {
	            	//找到用户
	            	Account account = Account.dao.findById(oauthUser.getUserId());
	            	System.out.println("qq_login_notify:" + account);
	    			setSessionAttr("userInfo", account);
//    	    			Long liveSeconds = (long) (keepLogin ? 365 * 24 * 60 * 60 : 120 * 60);
//    	    			int maxAgeInSeconds = (int) (keepLogin ? liveSeconds : -1);
//    	    			Long expireTime = System.currentTimeMillis() + (liveSeconds * 1000);
//    	    			if (keepLogin) {
//    	    				Session session = new Session();
//    	    				String sessionId = UUID.randomUUID().toString();
//    	    				session.setSessionId(sessionId);
//    	    				session.setUserId(account.getUserId());
//    	    				session.setExpireTime(expireTime);
//    	    				session.save();
//    	    				setCookie("cogTrainingId", sessionId, maxAgeInSeconds, true);
//    	    			}
	                redirect("/home");
	            }
	            else {
	            	setAttr("nickname", nickname);
	            	setAttr("openId", openId);
	                render("/bind.html");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            renderJson("msg", "系统错误");
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
