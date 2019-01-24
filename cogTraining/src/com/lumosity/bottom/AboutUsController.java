package com.lumosity.bottom;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.lumosity.common.UrlFilterInterceptor;
import com.lumosity.model.GameClass;
import com.lumosity.model.PriceBase;
import com.lumosity.model.TrainClass;
import com.lumosity.order.PayService;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 关于我们
 * @author yesong
 *
 */
@Clear({UrlFilterInterceptor.class})
public class AboutUsController extends Controller {

	public void index() {
		render("/bottom/about.html");
	}
	
	@ActionKey("/legal")
	public void legal(){
		render("/bottom/terms_of_service.html");
	}
	
	@ActionKey("/managerLogin")
	public void managerLogin(){
		render("/bottom/manager_login.html");
	}
	
	@ActionKey("/payment")
	public void payment(){
		List<Record> list = TrainClass.dao.findNormalList();
		for (Record trainClass : list) {
			Integer trainClassId = trainClass.getInt("trainClassId");
			List<GameClass> gameClassList = GameClass.dao.findByTrainClass(trainClassId);
			List<Record> gameClassWithPriceList = GameClass.dao.findGameClassPrice();
			List<Record> gameClassTimes = GameClass.dao.findGameClassTime();
			setAttr("gameClassList", gameClassList);
			setAttr("gameClassWithPriceList", gameClassWithPriceList);
			setAttr("gameClassTimes", gameClassTimes);
			StringBuffer buffer = new StringBuffer("");
			for (GameClass gameClass : gameClassList) {
				buffer.append(gameClass.getGameClassId() + ",");
			}
			trainClass.set("gameClassIds", buffer.substring(0, buffer.length() - 1));
			trainClass.set("priceBaseList", PriceBase.dao.findByTrainClassId(trainClass.getInt("trainClassId")));
		}
		
		setAttr("list", list);
		render("/bottom/payment.html");
	}

	/**
	 * 成为会员页 支付方案
	 */
	@ActionKey("/payment/param")
	public void paymentParam(){
		List<GameClass> gameClassList = GameClass.dao.findByTrainClass(1);
		StringBuffer buffer = new StringBuffer("");
		for (GameClass gameClass : gameClassList) {
			buffer.append(gameClass.getGameClassId() + ",");
		}

		JSONArray scheme= PayService.me.getScheme();
		JSONObject result=new JSONObject();
		result.put("code",1);
		result.put("msg","充值方案");
		result.put("scheme",scheme);
		result.put("gameClassIds",buffer.substring(0, buffer.length() - 1));
		renderJson(result.toString());



	}






	@ActionKey("/paySuccess")
    public void paymentResult(){
        render("/bottom/payment_success.html");
    }


	
	@ActionKey("/totalPrice")
	public void totalPrice() {
		Integer gameDateId = this.getParaToInt("gameDateId");
		String gameClassIds = this.getPara("gameClassIds");
		float totalPrice = GameClass.dao.totalPrice(gameDateId, gameClassIds);
		this.renderJson("totalPrice", totalPrice);
	}
	
	public List<Record> findGameDate() {
		return Db.find("select * from dict_data dd where dd.dict_type_id = 8");
	}
}
