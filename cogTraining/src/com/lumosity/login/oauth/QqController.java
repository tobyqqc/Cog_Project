//package com.lumosity.login.oauth;
//
//
//import com.jfinal.core.Controller;
//import com.lumosity.login.oauth.meta.ShareLoginDict;
//
//public class QqController extends Controller{
//
//	public void index() {
//		redirect("https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id="
//				+ ShareLoginDict.qqApiId + "&redirect_uri=" + ShareLoginDict.qqNotify
//				+ "&scope=get_user_info"
//				+ "&state=" + ShareLoginDict.qqState);
//	}
//}
