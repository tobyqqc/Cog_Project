package com.lumosity.settings.weixin;


import com.jfinal.core.Controller;
import com.lumosity.login.oauth.WxService;
import com.lumosity.model.Account;
import com.lumosity.model.OauthUser;

import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

public class WxPCLoginNotifyController extends Controller{
	
	public void index() {
		String code = this.getPara("code");
		try {
			WxMpOAuth2AccessToken wxMpOAuth2AccessToken = WxService.oauth2getAccessToken(code);
			WxMpUser wxMapUser = WxService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
//			WxMpUser wxMapUser = new WxMpUser();
//			wxMapUser.setOpenId("1000000063");
//			wxMapUser.setNickname("yesong000001");
//			wxMapUser.setHeadImgUrl("http://img.sj33.cn/uploads/allimg/200908/20090821004513914.jpg");
//			wxMapUser.setSex("男");
			System.out.println("openId:" + wxMapUser.getOpenId());
			
			String openId = wxMapUser.getOpenId();
			System.out.println("OPEN_ID:" + openId);
	    	String nickname = wxMapUser.getNickname();
	    	String cover = wxMapUser.getHeadImgUrl();
	    	Integer platform = 2;
	    	Integer gender = (wxMapUser.getSex().equals("男") ? 0 : 1);
	    	
	    	Long userId = this.getParaToLong("userId");
	    	if (userId == null) {
	    		System.out.println("用户ID为空");
	    		return;
	    	}
	    	setSessionAttr("userInfo", Account.dao.findById(userId));
	    	try {
				OauthUser oauthUser2 = OauthUser.dao.findByOpenId(openId);
				if (oauthUser2 != null ) {
					if (oauthUser2.getUserId() != null) {
						renderJson("msg", "微信号不能重复绑定");
					}
					else {
						oauthUser2.setUserId(userId);
						oauthUser2.update();
						redirect("/settings");
					}
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	
	    	OauthUser ouathUser = new OauthUser();
	    	ouathUser.setUserId(userId);
	    	ouathUser.setOpenId(openId);
	    	ouathUser.setNickname(nickname);
	    	ouathUser.setCover(cover);
	    	ouathUser.setGender(gender);
	    	ouathUser.setPlatform(platform);
	    	ouathUser.setCreateTime(System.currentTimeMillis());
	    	ouathUser.save();
	    	
	    	Account account = Account.dao.findById(userId);
	    	account.setWeixinId(openId);
	    	account.update();
	    	redirect("/settings");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
