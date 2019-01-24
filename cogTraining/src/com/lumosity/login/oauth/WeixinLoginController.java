package com.lumosity.login.oauth;

import com.jfinal.core.Controller;
import com.lumosity.login.oauth.meta.ShareLoginDict;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

import java.net.URLDecoder;

public class WeixinLoginController extends Controller {

	
	
	public void index() {
		try{
			String type = getPara("type");

			String to =getPara("to");

			//判断是否微信登录
/*
			WxMpUser wxMapUser = getSessionAttr("wxMpUser");
			if(wxMapUser!=null){
				if(to!=null){
					redirect(URLDecoder.decode(to,"utf-8"));
				}else{
					redirect("/");
				}
				return;
			}
*/


			//开始拼接 授权引导页地址
			if ("weixin".equals(type)) {
				// 跳转公众号登录
				String redirect_url=ShareLoginDict.weixinMpNotify;
				if(to!=null){
					redirect_url+="?to="+to;
				}

				String addr=WxService.oauth2buildAuthorizationUrl(redirect_url,
						ShareLoginDict.weixinMpScope, ShareLoginDict.weixinMpState);
				//System.out.println(addr);
				redirect(addr);
			}
			else {
				// 跳转网页登录
				redirect(WxService.oauth2buildQrconnectUrl(ShareLoginDict.weixinNotify,
						ShareLoginDict.weiScope, ShareLoginDict.weixinState));
			}
		}catch (Exception e){

		}

	}
}
