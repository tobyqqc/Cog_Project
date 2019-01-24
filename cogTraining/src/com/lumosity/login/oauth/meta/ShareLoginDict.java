package com.lumosity.login.oauth.meta;

public interface ShareLoginDict {

	String PRODUCT_URL = "http://www.cogdaily.net";

	String DEV_URL= "http://cvm.gohigh.top";
	
	String DOMAIN_URL = PRODUCT_URL;
	
	//*****************************************************************
	//************************QQ信息********************************
	//*****************************************************************
	String qqApiId = "101396493";
	String qqAppKey = "9881b67afe83a26eddc9dc876dba67b3";
	String qqState = "colTraining";
	String qqNotify = DOMAIN_URL + "/notify";
	
	//*****************************************************************
	//************************微信信息********************************
	//*****************************************************************
	
	
	
	// 微信网页端配置信息(开放平台)
	String weixinPCAppid = "wx2b2dc2e5fcd952d2";
	String WeixinPCAppSecret = "dbd519685661e98fe8bdce7e8262b361";
	
	// 微信移动端配置信息(开放平台)
	String weixinAppid = "wxc9a464ba8f6bc447";
	String WeixinAppSecret = "a5cdd5c7e0c364860ab103db0a40fe8d";
	
	String weiScope = "snsapi_login";
	String weixinState = "STATE";
	
	// 微信移动端登录回调
	String weixinNotify = DOMAIN_URL + "/wxLoginNotify";

	//微信移动端登录回调 重做版
	String WXLoginNotify=DOMAIN_URL+"/wxlogin";

	// 微信网页端登录回调
	String weixinPCLoginNotify = DOMAIN_URL + "/weixinPCLoginNotify";
	
	
	// 微信支付信息(商户平台)
	String WEIXIN_PARTNERID = "1497002322";
	String WEIXIN_PARTNERKEY = "5D1FCC1C7D5E46B6B218DA572345D56B"; 
	String WEIXIN_DOMAIN = DOMAIN_URL;
	String weixinPayNotify = DOMAIN_URL + "/wxPayNotify";

	// 微信移动端配置信息(公众平台)
/*	String weixinMpAppid = "wx5b0e92660eb66942";
	String WeixinMpAppSecret = "99a2fdd79ffd614299bf7397e61ed524";*/
//新的

/*	String weixinMpAppid = "wx5b0e92660eb66942";
	String WeixinMpAppSecret = "018870e2aa01bac80bda9f64491941ae";*/


String weixinMpAppid = "wx8611c207e691add7";
	String WeixinMpAppSecret = "8068cc1120b3d57aac26d4c4a85f5706";


	String weixinMpScope = "snsapi_userinfo";
	String weixinMpState = "weixin_state";
	String weixinMpToken = "burgwb725leafcogdaily";
	String weixinMpAESKEY = "dVxEXHxXBFACOgJnznJWjxa1jxubW77oK5A1OAWRWOB";
	String weixinMpNotify = DOMAIN_URL + "/wxMpLoginNotify";
	
	//*****************************************************************
	//************************支付宝信息********************************
	//*****************************************************************
	String ALIPAY_NOTIFY_URL = DOMAIN_URL + "/alipayPayNotify";
	String ALIPAY_APPID = "2018020502145600";
	String ALIPAY_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCJNijpdMWZQNBAlsTV0J7OVw4BUC1dJ5hy2Yt0qnaV435g7SPGYKA8l1p8eYzhtBEh0XSzwAxjpnnMRyE3kcfy7sNstQ1rJjRCEUAexyOHv6r9XNkZ5JMnvYE6qY/cYeW+yXOgKd78/a55R0hir9tIy9nOvA7j/mb23FmrUne+KwimVF21M495Eft1XOhzq+CQ/BztagDU9yVDnb//vAihfqQNC2GR4ZQpZtKUYGPGaT5yoFVTEL5dg7G2drxy/Zkanmz41vf8BXf7j5NNq54Rnf9qCJ9QVpJDFJy/SIAIOS8tVDJEJCKxDxGAlZRqDALp5VB+wY5HTQOQQPwPjIV7AgMBAAECggEAN8VJz8pk3qvXeclObFIFmn0L9kCuj6SedGA0LNF0revv3FnHJsTznG0wnFOTjdHw/Lv0IXO4FXD/JKbsu3eHvBiMWk0lDR5UM15rn5b1vCGN0LnCjKG5pneCZWOyU7N+/JNJahpaxp4eIrF0nJg6G1awSzVpIAaxtzlNQTOd9C9SRnXFMx5/sJcCxxT9fm3vgUbxGBVnS/9n9PfGGScbXGURkCFPVdbrfNaCHlwFyR8//KxKvcYOF3NEwmkRsFj911cTlR+5b9elukcRdDvbEUbqNOSE3V4vHfm29gIfQVOiwa39WROqmwT01WRDNEz9k1u5MFqrZDzoLcXAzeaOwQKBgQDk2aTjEb7hzV/eDq+Oo+JB6K0ptKv4D64Q9FHSi4D0lEEUlUzRKdObYomDrxnjTr2PCIOvVbZ5ez4Wq2RJLgISgAGInY8u7NgVyehrS4qesLfpFLCpcJSWqqKRYHC2oiLWY/QgciIJl0SAzcw2Q56Sh0y4zv/r3hgI40i5Qu9BSwKBgQCZfWFtXiE26jup+ui8tSJN9RwgiLvjMzOhJ7lNVkX9oEFT/Mz3KxWNGuDHJqS9RUiIzQm4v5bG6pnAbjbB1HVOXHHFbbg9drpQR/D3WSqsGo0jlAjP5SYPZChEOlUczXKMK4csfQYeQ7c7DDqhIPKewuEMlfubO6HDnwQnKc1ekQKBgFWxGYjdMDilCmy/2wTyYNqFIbIoMOymEmvO0ISiMBtlGCl47laz7sSUVSG2A1+WMGzKp2SUF8vNViZu7xJn4+bBf9GEDgVoMlGWVS04ECPz0Cm60ZvgyALSyiTdk9cPRzQ4IUW09wHYXpJMfVckYtBDUXsyJ6pOc3mAgc69PZGZAoGALX51PzZ0P9OjhZliHHTPyy2IfbgpgIkvl2evy9Vzf15dnqhClExH4gACeeThVVxQyIOFiyX2adUlZ10Di+YOMwDBlBQWVNl5FcyNBrN+K27fQJljF0PDSTKVoMttaNEYT6WjNjOEebn2eGECi7m5UAdRLyrRXcyKAPKEB5ujTJECgYALgAmYDVA/XMqocL5JFthdiKSV1m0svmPOad7HcpWQRVNOVTueOS+hX1WpQTAzqpKwNpc1MKbLpIhoOOMiYFi36pbbTysRhF4KpayiS5y4ptaZl1QckxoJKJOLxqSCnM4iNnRK+nCgrFz4p0tZT27UnZokihp+1oZA9+pexi4XBA==";
	String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAta+8ARRaT//5l+QStnzgVNruUA9+j/79u4JsXop39AGg+6HBcgZo0nB08Oc+Bw+ueXslWBJPscfeAU9iOVP11rVgqxTJtX9F0gjUvyLNdldHL2DMKFdoRM9h0zGFA9bzox/Nwbdvo4jaWbB7vRFCq3SEZKJD47rNnJexgUOQ3MHXasdP/6y+6grczRLN6jkcrG2wGhSQKlqdG2bWUGiMZSwI9NRH0Ggak7RivhA8/k2w3t506FwqZ4A+5y4rOhXZwiIeaVn+kw4PjsAzJOCfLUJJZdnq3EcXERuhkM4aEUjFydGDVIxhcwmBbtK8bZtffW2nVLYj+tb2mlaxmuuUaQIDAQAB";


	/**
	 * 微信公众号accesstoken 获取地址
	 */
	public final  static String WXAccessTokenAddr="https://api.weixin.qq.com/cgi-bin/token";
	public final  static String WX_JSAPI_TICKET_ADDR="https://api.weixin.qq.com/cgi-bin/ticket/getticket";



}
