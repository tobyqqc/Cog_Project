package com.lumosity.model;

import com.lumosity.model.base.BaseWxAppConfig;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class WxAppConfig extends BaseWxAppConfig<WxAppConfig> {

	public static final WxAppConfig dao = new WxAppConfig();
	public WxAppConfig findByAppid(String appid){
	    try{
            WxAppConfig wxAppConfig=findFirst("select * from wx_app_config where appid=?",appid);
	        return wxAppConfig;
        }
        catch (Exception e){
	        e.printStackTrace();
	        return  null;
        }
    }
}
