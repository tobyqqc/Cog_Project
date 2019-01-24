package com.lumosity.settings.mobile;

import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.lumosity.model.Account;

public class MobileController extends Controller{

	/**
	 * 修改手机逻辑
	 * 1.接收传参
	 * 2.修改数据库
	 * 3.跳转/settings
	 */
	public void index() {
		Account account = getSessionAttr("userInfo");
		
		String mobile = getPara("mobileId");
		String code = getPara("code");
		Account valiAccount = Account.dao.findByPhone(mobile);
		if (valiAccount != null) {
			//修改邮箱已经被占用,不允许修改,跳转修改页面
			setAttr("msg", "手机已经被占用");
			forwardAction("/settings/login_info_mobile/edit");
			return;
		} 
		if (!code.equals(CacheKit.get("valiCode", mobile))) {
			setAttr("msg", "无效的验证码！");
			forwardAction("/settings/login_info_mobile/edit");
			return;
		}
		
		if ("".equals(mobile.trim()) || mobile == null) {
			redirect("/settings/login_info_mobile/edit");
			setAttr("msg", "手机不能为空");
		} else {
			account.set("mobileId", mobile).update();
			redirect("/settings");
		}
	}
	
	public void edit() {
		render("/settings/mobile/change_mobile.html");
	}
}
