package com.lumosity.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipaySubmit;
import com.jfinal.core.Controller;
import com.lumosity.model.Account;
import com.lumosity.model.Order;
import com.lumosity.utils.OrderKit;
/**
 *功能：即时到账交易接口接入页
 *版本：3.4
 *修改日期：2016-03-08
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。

 *************************注意*****************
 *如果您在接口集成过程中遇到问题，可以按照下面的途径来解决
 *1、开发文档中心（https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.KvddfJ&treeId=62&articleId=103740&docType=1）
 *2、商户帮助中心（https://cshall.alipay.com/enterprise/help_detail.htm?help_id=473888）
 *3、支持中心（https://support.open.alipay.com/alipay/support/index.htm）
 *如果不想使用扩展功能请把扩展功能参数赋空值。
 **********************************************
 * @author Scott
 *
 */
public class AlipayApi extends Controller {

	public void index() throws Exception {
		////////////////////////////////////请求参数//////////////////////////////////////
		String gameClassIds = this.getPara("gameClassIds");
		Account account = getSessionAttr("userInfo");
		if (StringUtils.isNotBlank(gameClassIds)) {
			String[] gameClassIdss = gameClassIds.split(",");
//			String trainClassOrderNo =  OrderKit.generateOrderNo();
			String trainClassOrderNo = this.getPara("orderNo");
			//商户订单号，商户网站订单系统中唯一订单号，必填
//			String out_trade_no = new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"),"UTF-8");
//			String out_trade_no = getPara("WIDout_trade_no");
			
			//订单名称，必填
			String subject = "游戏训练产品";
//			String subject = getPara("WIDsubject");
			
			//付款金额，必填
//			String total_fee = new String(request.getParameter("totalPrice"));
//			System.out.println("total_fee2:" + total_fee);
//			String total_fee = "0.01";
//			String total_fee = getPara("WIDtotal_fee");

			//商品描述，可空ISO-8859-1
			Integer ddId = this.getParaToInt("gameDate");
			String body = new String((ddId * 30) + "天训练套餐");
			String total_fee=String.format("%.2f",PayService.me.getCostByDuration(ddId));
//			String body = getPara("WIDbody");
			
			
			if (StringUtils.isNotBlank(gameClassIds)) {
				gameClassIds = gameClassIds.substring(0, gameClassIds.length() - 1);
			}
			//把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("service", AlipayConfig.service);
	        sParaTemp.put("partner", AlipayConfig.partner);
	        sParaTemp.put("seller_id", AlipayConfig.seller_id);
	        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
			sParaTemp.put("payment_type", AlipayConfig.payment_type);
			sParaTemp.put("notify_url", AlipayConfig.notify_url);
			sParaTemp.put("return_url", AlipayConfig.return_url);
			sParaTemp.put("anti_phishing_key", AlipayConfig.anti_phishing_key);
			sParaTemp.put("exter_invoke_ip", AlipayConfig.exter_invoke_ip);
			sParaTemp.put("out_trade_no", trainClassOrderNo);
			sParaTemp.put("subject", subject);
			sParaTemp.put("total_fee", total_fee);
			sParaTemp.put("body", body);
			//其他业务参数根据在线开发文档，添加参数.文档地址:https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.O9yorI&treeId=62&articleId=103740&docType=1
	        //如sParaTemp.put("参数名","参数值");
			//建立请求
			String sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"get","ok");
			
			for (String gameClassId : gameClassIdss) {
				String out_trade_no = OrderKit.generateOrderNo();
				Order.dao.saveOrder(out_trade_no, account.getUserId(), new BigDecimal(total_fee), "支付宝", gameClassId, (ddId * 30), trainClassOrderNo);
			}
			renderHtml(sHtmlText);
		}
	}
	
	public void generateOrderNo() {
		String orderNo = OrderKit.generateOrderNo();
		renderJson("data", orderNo);
	}
}
