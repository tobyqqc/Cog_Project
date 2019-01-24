package com.lumosity.login.oauth;

import com.jfinal.core.Controller;
import com.lumosity.model.Account;

public class CheckMobileController extends Controller {

	public void index() {
	   	String mobile = this.getPara("mobile");
        try {
        	Account account = Account.dao.isAccountExist(mobile);
            boolean isExit = (account!= null && account.get("userId") != null);
            renderJson("isExistisExist", (isExit ? "1" : "0"));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson("msg", "系统错误");
        }
	}
}
