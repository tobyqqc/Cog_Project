package com.lumosity.login.oauth;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.core.Controller;
import com.lumosity.login.oauth.meta.ShareLoginDict;
import com.lumosity.model.OauthUser;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

public class WeixinMpAuthorizationController extends Controller {

	private static final String redirect_url = ShareLoginDict.PRODUCT_URL + "weixinMp/redirect";
	
	public void authorization() {
		HttpServletRequest request = this.getRequest();
		OauthUser wxUser = getSessionAttr("wxUser");
		if (wxUser == null) {
			String redirectUrl = redirect_url + "?redirectUrl=" + (request.getQueryString() != null ? 
					request.getRequestURI() + "?" + request.getQueryString() : request.getRequestURI());
			
		}
	}
	
	public void redirect() {
		HttpServletRequest request = this.getRequest();
		String code = request.getParameter("code");
		System.out.println("weixin code:" + code);
		String redirectUrl = request.getParameter("redirectUrl");
		System.out.println("redirectUrl:" +redirectUrl);
		
		WxMpOAuth2AccessToken wxMpOAuth2AccessToken;
		try {
			wxMpOAuth2AccessToken = WxService.oauth2getAccessToken(code);
			WxMpUser wxMapUser = WxService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
		} catch (WxErrorException e) {
			e.printStackTrace();
		}
		
		
	}
}
