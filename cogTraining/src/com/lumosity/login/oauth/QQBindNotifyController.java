package com.lumosity.login.oauth;

import com.jfinal.core.Controller;
import com.lumosity.model.Account;
import com.lumosity.model.OauthUser;


public class QQBindNotifyController extends Controller {

	public void index() {
	//	render("/settings/qqLoginBindNotify.html");
		try {
//			WxMpUser wxMapUser = new WxMpUser();
//			wxMapUser.setOpenId("1000000063");
//			wxMapUser.setNickname("yesong000001");
//			wxMapUser.setHeadImgUrl("http://img.sj33.cn/uploads/allimg/200908/20090821004513914.jpg");
//			wxMapUser.setSex("男");
			String openId = this.getPara("openId");
			String nickname = "";
			String genderName = "";
			
			System.out.println("OPEN_ID:" + openId);
	    	String cover = "";
	    	Integer platform = 1;
	    	Integer gender = (genderName.equals("男") ? 0 : 1);
	    	
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
						render("/settings/qqLoginBindNotify.html");
						//renderJson("msg", "QQ号不能重复绑定");
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
	    	account.setQqId(openId);
	    	account.update();
	    	redirect("/settings");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void unbind() {
		Account account = getSessionAttr("userInfo");
		String openId = getPara("openId");
		OauthUser.dao.unbindQQ(openId, account.getUserId());
		redirect("/settings");
	}
}
