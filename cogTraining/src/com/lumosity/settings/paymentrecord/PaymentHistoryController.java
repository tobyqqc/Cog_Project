package com.lumosity.settings.paymentrecord;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.lumosity.model.Account;
import com.lumosity.model.Order;
import com.lumosity.utils.JqGridModelUtils;

public class PaymentHistoryController extends Controller{

	public void index() {
		Account account = getSessionAttr("userInfo");
		String password = getPara("password");
		String password_confirmation = getPara("password_confirmation");
		if (password == null || "".equals(password.trim()) 
			|| password_confirmation == null || "".equals(password_confirmation.trim())) {
			setAttr("message", "不能为空");
		} else {
			
			if (password.equals(password_confirmation)) {
				account.set("password", password).update();
				redirect("/settings");
			} else {
				forwardAction("/settings/password/edit");
				setAttr("message", "请输入相同密码");
			}
		}
	}
	
	public void page() {
		int pageNum = this.getParaToInt("page", 1);
		int pageSize = getRows();
		Account account = getSessionAttr("userInfo");
		Page<Record> pageInfo = Order.dao.page(account.getUserId(), pageNum, pageSize);
        this.renderJson(JqGridModelUtils.toJqGridView(pageInfo));
	}
	
	public void edit() {
		render("/settings/payment_history/payment_history.html");
	}
	public void edit2() {
		render("/settings/payment_history/payment_history_table.html");
	}
	protected int getRows(){
		int rows=this.getParaToInt("rows", 20);
		if(rows>1000)rows=1000;
		return rows;
	}
}
