package com.lumosity.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.core.Controller;
import com.lumosity.login.oauth.WxService;
import com.lumosity.login.oauth.meta.ShareLoginDict;
import com.lumosity.model.Account;
import com.lumosity.model.GameClass;
import com.lumosity.model.Order;
import com.lumosity.utils.OrderKit;

import me.chanjar.weixin.mp.bean.result.WxMpUser;

public class WxPayController extends Controller {
	
	public void index() {



		try {

			Integer ddId = this.getParaToInt("gameDate");
			String payinfo="会员充值"+ddId+"个月";

			float amount=PayService.me.getCostByDuration(ddId);

			String type = this.getPara("type");
			String gameClassIds = this.getPara("gameClassIds");
			Account account = getSessionAttr("userInfo");
			String[] gameClassIdss = gameClassIds.split(",");



			String trainClassOrderNo =  OrderKit.generateSimpleOrderNo();

			for (String gameClassId : gameClassIdss) {
				String out_trade_no = OrderKit.generateOrderNo();
				Order.dao.saveOrder(out_trade_no, account.getUserId(), new BigDecimal(amount), "微信", gameClassId, ddId * 30, trainClassOrderNo);
			}

			// 创建订单
//		String outTradeNo = UUID.randomUUID().toString().replace("-", "");
			String ip = this.getRequest().getRemoteAddr();
			String callbackUrl = ShareLoginDict.weixinPayNotify;

			if ("weixin".equals(type)) {
				WxMpUser wxMapUser = getSessionAttr("wxMpUser");
				System.out.println("wxMpUser.OpenId:" + (wxMapUser == null ? "" : wxMapUser.getOpenId()));
				if (wxMapUser == null) {
					Map result= new HashMap(2);
					result.put("code",0);
					result.put("msg","wxNoLogin");
					renderJson(result);
				}
				else {
					Map result = WxService.getJSSDKPayInfo(wxMapUser.getOpenId(), trainClassOrderNo, amount, payinfo, "JSAPI", ip, callbackUrl);
					result.put("code",1);
					renderJson("data", result);
				}
			} 
			else {
				String codeUrl = WxService.getNativeSDKPayInfo("", trainClassOrderNo, amount, payinfo, "NATIVE", ip, callbackUrl);
				Map result = new HashMap(2);
				result.put("qrcodeUrl", codeUrl);
				result.put("orderNo", trainClassOrderNo);
				result.put("amount",amount);
				result.put("code",1);

				renderJson( result);
			}
		}catch (PayService.PayServiceException e){
			Map result = new HashMap(2);
			result.put("code",0);
			result.put("msg",e.getMessage());
			renderJson(result);
			return;
		}
		catch (Exception e) {
			e.printStackTrace();
			Map result = new HashMap(2);
			result.put("code",0);
			result.put("msg","支付数据异常，请尝试重试！");
			renderJson(result);
			return;
		}
	}
}
