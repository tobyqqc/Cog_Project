package com.lumosity.order;

import me.chanjar.weixin.mp.bean.result.WxMpPrepayIdResult;

public class MyWxMpPrepayIdResult extends WxMpPrepayIdResult {
	private static final long serialVersionUID = 6393988969927251414L;
	
	
	private String code_url;


	public String getCode_url() {
		return code_url;
	}


	public void setCode_url(String code_url) {
		this.code_url = code_url;
	}
	
	
}
