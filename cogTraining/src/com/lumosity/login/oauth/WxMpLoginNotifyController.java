package com.lumosity.login.oauth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.Controller;
import com.lumosity.model.Account;
import com.lumosity.model.OauthUser;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

public class WxMpLoginNotifyController extends Controller{
	
	public void index() {
		String code = this.getPara("code");
		System.out.println("code=" + code);
		HttpServletRequest request = this.getRequest();
		HttpServletResponse response = this.getResponse();
		try {
			 String requestType = request.getHeader("X-Requested-With");
             if (requestType != null && requestType.equalsIgnoreCase("XMLHttpRequest")) {
                 response.addHeader("loginStatus", "accessDenied");
                 response.sendError(HttpServletResponse.SC_FORBIDDEN);
                 renderJson("error");
             } else {
     			WxMpOAuth2AccessToken wxMpOAuth2AccessToken = WxService.oauth2getMpAccessToken(code);
    			WxMpUser wxMapUser = WxService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
    			
    			setSessionAttr("wxMpUser", wxMapUser);

//    			WxMpUser wxMapUser = new WxMpUser();
//    			wxMapUser.setOpenId("100000003");
//    			wxMapUser.setNickname("yesong000001");
//    			wxMapUser.setHeadImgUrl("http://img.sj33.cn/uploads/allimg/200908/20090821004513914.jpg");
//    			wxMapUser.setSex("男");
//    			
    			System.out.println("openId:" + wxMapUser.getOpenId());
    			String openId = wxMapUser.getOpenId();
    	    	String nickname = wxMapUser.getNickname();
    	    	String cover = wxMapUser.getHeadImgUrl();
    	    	Integer platform = 2;
    	    	Integer gender = (wxMapUser.getSex().equals("男") ? 0 : 1);
    	    	
    	        OauthUser oauthUser = new OauthUser();
    	        oauthUser.setOpenId(openId);
    	        oauthUser.setCover(cover);
    	        oauthUser.setNickname(nickname);
    	        oauthUser.setPlatform(platform);
    	        oauthUser.setGender(gender);
    	        try {
    	            boolean flag = OauthUser.dao.oauthLogin(oauthUser);
    	            System.out.println("oauth_flag:" + flag);
    	            if (flag) {
    	            	//找到用户
    	            	Account account = Account.dao.findById(oauthUser.getUserId());
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
						String to=getPara("to");
						if(to!=null){
							redirect(to);
						}else{
							redirect("/home");
						}

    	            }
    	            else {
    	            	setSessionAttr("wxMpUser",null);
						String to=getPara("to");
						setAttr("to",to);
    	            	setAttr("nickname", nickname);
    	            	setAttr("openId", openId);
    	                render("/bind.html");
    	            }
    	        } catch (Exception e) {
					setSessionAttr("wxMpUser",null);
    	            e.printStackTrace();
    	            renderJson("msg", "系统错误");
    	        }
             }
		} catch (Exception e) {
			setSessionAttr("wxMpUser",null);
			e.printStackTrace();
		}
	}
}
