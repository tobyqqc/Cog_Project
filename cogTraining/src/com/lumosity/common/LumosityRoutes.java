package com.lumosity.common;

import com.jfinal.config.Routes;
import com.lumosity.admin.AdminController;
import com.lumosity.api.ApplePayController;
import com.lumosity.api.AuthApiController;
import com.lumosity.api.PayController;
import com.lumosity.api.StatsController;
import com.lumosity.bottom.AboutUsController;
import com.lumosity.games.FitTestController;
import com.lumosity.games.GamesController;
import com.lumosity.home.HomeController;
import com.lumosity.login.ForgotController;
import com.lumosity.login.LoginController;
import com.lumosity.login.LogoutController;
import com.lumosity.login.oauth.*;
import com.lumosity.order.AlipayApi;
import com.lumosity.order.AlipayNotifyController;
import com.lumosity.order.NotifyController;
import com.lumosity.order.OrderController;
import com.lumosity.order.ReturnController;
import com.lumosity.order.WxNotiyfyController;
import com.lumosity.order.WxPayController;
import com.lumosity.question.QuestionController;
import com.lumosity.register.RegisterController;
import com.lumosity.settings.SettingsController;
import com.lumosity.settings.email.EmailController;
import com.lumosity.settings.email.NotificationController;
import com.lumosity.settings.mobile.MobileController;
import com.lumosity.settings.password.PasswordController;
import com.lumosity.settings.paymentrecord.PaymentHistoryController;
import com.lumosity.settings.personal.ChangePersonalController;
import com.lumosity.settings.preferences.PreferencesController;
import com.lumosity.settings.reminder.ReminderController;
import com.lumosity.settings.weixin.WeixiPcUnbindController;
import com.lumosity.settings.weixin.WeixinPCLoginController;
import com.lumosity.settings.weixin.WxPCLoginNotifyController;
import com.lumosity.stats.brain_profile.BrainProfileController;
import com.lumosity.stats.comparison.ComparisonController;
import com.lumosity.stats.detail.DetailController;
import com.lumosity.stats.summaries.SummariesController;
import com.lumosity.train.TurboController;
import com.lumosity.utils.TestController;

/**
 * 路由管理
 * @author Scott
 *
 */
public class LumosityRoutes extends Routes{

	@Override
	public void config() {

		add("/", IndexController.class);
		add("/login", LoginController.class);
		add("/forgot", ForgotController.class);
		add("/logout", LogoutController.class);
		add("/register", RegisterController.class);
		add("/question", QuestionController.class);
		add("/games", GamesController.class);
		add("/fitTest", FitTestController.class);
		add("/aboutUs", AboutUsController.class);
		
		add("/home", HomeController.class);
		
		add("/settings", SettingsController.class);
		add("/settings/login_info", EmailController.class);
		add("/settings/login_info_mobile", MobileController.class);
		add("/settings/password", PasswordController.class);
		add("/settings/personal_info", ChangePersonalController.class);
		add("/settings/payment_history", PaymentHistoryController.class);
		add("/settings/email_notifications", NotificationController.class);
		add("/settings/training_preferences", PreferencesController.class);
		add("/settings/reminder", ReminderController.class);
		
		add("/brain_profile", BrainProfileController.class);
		add("/training_history", SummariesController.class);
		add("/training_history/summaries", SummariesController.class);
		add("/training_history/detail", DetailController.class);
		add("/comparison", ComparisonController.class);
		
		add("/train/turbo", TurboController.class);
		
		
		add("/order", OrderController.class);
		add("/alipayapi", AlipayApi.class);
		add("/notify", NotifyController.class);
		add("/return", ReturnController.class);
		
		add("/api/auth", AuthApiController.class);
		add("/api/page", StatsController.class);
//		add("/api/pay", PayController.class);
		add("/api/auth/apply/verify", ApplePayController.class);
		
//		add("/sql/de", DeleteController.class);
//		add("/oauth", QqController.class);
		
		add("/weixinLogin", WeixinLoginController.class);
		add("/checkSignature", WeixincheckSignatureController.class);
		add("/wxLoginNotify", WxLoginNotifyController.class);
		
		add("/wxMpLoginNotify", WxMpLoginNotifyController.class);
		add("/qqLoginNotify", QQLoginNotifyController.class);
		
		add("/checkMobile", CheckMobileController.class);
		add("/wxLoginBind", WeixinLoginBindController.class);
		
		add("/weixinPCLogin", WeixinPCLoginController.class);
		add("/weixinPCLoginNotify", WxPCLoginNotifyController.class);
		add("/qqLoginBindNotify", QQBindNotifyController.class);
		add("/weixinUnbind", WeixiPcUnbindController.class);
		
		
		// 网页微信支付
		add("/wxPay", WxPayController.class);
		// 微信回调，包括网页与APP
		add("/wxPayNotify", WxNotiyfyController.class);
		
		// APP支付，包括微信与支付宝
		add("/appPay", PayController.class);
		// 支付宝回调
		add("/alipayPayNotify", AlipayNotifyController.class);

		add("/test", TestController.class);


		add("/wxlogin",WXLoginController.class);

		add("/admin", AdminController.class);

	
		
	}
}
