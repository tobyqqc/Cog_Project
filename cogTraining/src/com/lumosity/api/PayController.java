package com.lumosity.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.lumosity.login.oauth.AliPayService;
import com.lumosity.login.oauth.WxService;
import com.lumosity.model.GameClass;
import com.lumosity.model.Order;
import com.lumosity.order.PayService;
import com.lumosity.utils.OrderKit;

@Clear
public class PayController extends Controller {
	
	@ActionKey("/api/auth/selectparams")
	public void selectParams() {
		List<Record> gameClassPrices = GameClass.dao.findGameClassPrice();
		List<Record> gameClassTimes = GameClass.dao.findGameClassTime();
		
		List<Record> sinClassPrices = new ArrayList<>();
		Record sinClassPrice = new Record();
		sinClassPrice.set("gameClassId", 1);
		int totalPrice = 0;
		for (Record record : gameClassPrices) {
			totalPrice += record.getBigDecimal("price").intValue();
		}
		sinClassPrice.set("price", totalPrice);
		sinClassPrice.set("gameClassName", "全部训练");
		sinClassPrices.add(sinClassPrice);
		
		setAttr("msg", "success");
		setAttr("gameClassPrices", sinClassPrices);
		setAttr("gameClassTimes", gameClassTimes);
		renderJson(new String[] {"msg", "gameClassPrices", "gameClassTimes"});
	}
	
	@ActionKey("/api/auth/pay/params")
	public void wxParams() {
		try{
			//String gameClassIds = this.getPara("gameClassIds");
			String gameClassIds = "1,2,3,4,5";
			Long userId = this.getParaToLong("userId");
			String type = this.getPara("payType");
			String[] gameClassIdss = gameClassIds.split(",");

		//	String total_fee = "0.01";
			Integer ddId = this.getParaToInt("gameDate");
			float total_fee=PayService.me.getCostByDuration(ddId/30);
			String trainClassOrderNo =  OrderKit.generateSimpleOrderNo();
			for (String gameClassId : gameClassIdss) {
				String out_trade_no = OrderKit.generateOrderNo();
				Order.dao.saveOrder(out_trade_no, userId, new BigDecimal(total_fee), "微信", gameClassId, ddId, trainClassOrderNo);
			}
			Map<String, String> params = null;
			if ("wxpay".equals(type)) {
				params = WxService.getParameterMap(trainClassOrderNo, new BigDecimal(total_fee), "会员购买", this.getRequest(), WxService.WEIXIN_OPEN);

			}
			else {
				params = AliPayService.getParameterMap(trainClassOrderNo, new BigDecimal(total_fee), "会员购买", this.getRequest());
			}
			setAttr("params", params);
			setAttr("msg", "success");
			renderJson(new String[]{ "msg", "params"});

		}catch (PayService.PayServiceException e){
			e.printStackTrace();
			setAttr("params",null);
			setAttr("msg", "fail");
			renderJson(new String[]{ "msg", "params"});

		}

	}
}
