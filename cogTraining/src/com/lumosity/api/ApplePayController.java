package com.lumosity.api;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.lumosity.model.IosReceipt;
import com.lumosity.model.Order;
import com.lumosity.test.ApplePayReturnClass;
import com.lumosity.utils.FileKit;
import com.lumosity.utils.IOS_Verify;
import com.lumosity.utils.OrderKit;

@Clear
public class ApplePayController extends Controller {

	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	
	@SuppressWarnings("unused")
	@ActionKey("/api/auth/apply/verify")
	public void applyVerify() {
		HttpServletRequest request = this.getRequest();
		String receipt = this.getPara("receipt");
		Long userId = this.getParaToLong("userId");
		// 类型 : iphone / ipad
		String type = this.getPara("type");

		String verifyState = this.getPara("verifyState");

		System.out.println("  来自苹果端的验证...");
		// receipt = " {'receipt-data' :
		// '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'}";
		receipt = receipt.replace("\'", "\"");
		try {
			// 查询数据库，看是否是己经验证过的账号
			// boolean isExists = iosReceiptService.findByReceipt(receipt);
			boolean isExists = IosReceipt.dao.isExist(receipt);
			String verifyResult;
			if (!isExists) {
				boolean setting = true;
				for (int i = 0; i < 5; i++) {
					verifyResult = IOS_Verify.buyAppVerify(receipt, setting);
					System.out.println("验证结果     " + verifyResult);
					if (StringUtils.isEmpty(verifyResult)) {
						// 苹果服务器没有返回验证结果
						System.out.println("苹果服务器没有返回验证结果");
						// return Result.failure(Constant.ERROR_22);
						renderJson("msg", "苹果服务器没有返回验证结果");
						return;
					} else {
						// 跟苹果验证有返回结果------------------
						JSONObject job = JSONObject.parseObject(verifyResult);
						String states = job.getString("status");
						
						/**
						21000 App Store无法读取你提供的JSON数据  
						21002 收据数据不符合格式  
						21003 收据无法被验证  
						21004 你提供的共享密钥和账户的共享密钥不一致  
						21005 收据服务器当前不可用  
						21006 收据是有效的，但订阅服务已经过期。当收到这个信息时，解码后的收据信息也包含在返回内容中  
						21007 收据信息是测试用（sandbox），但却被发送到产品环境中验证  
						21008 收据信息是产品环境中使用，但却被发送到测试环境中验证 
						**/
						if (states.equals("0")) {
							// 验证成功
							JSONArray jsonArray = job.getJSONObject("receipt").getJSONArray("in_app");
							List<ApplePayReturnClass> result = JSONArray.parseArray(jsonArray.toJSONString(), ApplePayReturnClass.class);
							int j = 0;
							long lastTime = 0L;
							try {
								for (int m = 0; m < result.size(); m++) {
									ApplePayReturnClass demoJson2 = result.get(m);
									System.out.println("demo:" + demoJson2.getTransaction_id());
									String lastTimeStr = demoJson2.getPurchase_date();
									long time = formatter.parse(lastTimeStr).getTime();
									if (lastTime == 0L) {
										lastTime = time;
									}
									else {
										if (lastTime < time) {
											lastTime = time;
											j = m;
										}
									}
								}
								ApplePayReturnClass lastDemoJson = result.get(j);
								System.out.println("lastDemo:" + lastDemoJson.getTransaction_id());
							} catch (ParseException e) {
								e.printStackTrace();
							}
							JSONObject returnJson = jsonArray.getJSONObject(j);
							// 产品ID
							String product_id = returnJson.getString("product_id");
							// 交易日期
							String purchase_date = returnJson.getString("purchase_date");
							String productJson = "";
							if ("iphone".equals(type)) {
							    productJson = FileKit.readUtf8String(
										request.getSession().getServletContext().getRealPath("/static/ios/product-iphone.json"));
							}
							else {
								productJson = FileKit.readUtf8String(
										request.getSession().getServletContext().getRealPath("/static/ios/product-ipad.json"));
							}
		
							JSONObject productJob = JSONObject.parseObject(productJson).getJSONObject(product_id);

							BigDecimal time = productJob.getBigDecimal("time");
							BigDecimal price = productJob.getBigDecimal("price");
							System.out.println("time：" + time + "====price:" + price);
							String[] gameClassIdss = new String[] { "1", "2", "3", "4", "5" };

							String total_fee = "0.01";
							String trainClassOrderNo = OrderKit.generateOrderNo();
							for (String gameClassId : gameClassIdss) {
								String out_trade_no = OrderKit.generateOrderNo();
								Order.dao.saveOrder(out_trade_no, userId, price, "苹果内购", gameClassId, time.intValue(),
										trainClassOrderNo);
							}

							Order.dao.successOrder(trainClassOrderNo);

							// 保存到数据库
							// iosReceiptService.save(receipt);
							IosReceipt.dao.save(receipt);
							Map<String, Object> map = new HashMap<>();
							map.put("amount", price.intValue());
							// return Result.success(map, "res");
							setAttr("msg", "success");
							setAttr("res", map);
							
							long expireTime = Order.dao.findExpireTimeByUserIdAndTrainClassId(userId, 1);
							// 默认未解锁
							int isLocked = 0;
							List<Integer> gameIds = new ArrayList<>();
							int day = -1;
							if (expireTime > System.currentTimeMillis()) {
								// 已解锁,会员未到期
								isLocked = 1;
							    long t = expireTime - System.currentTimeMillis();
							    day = (int) (t / 3600 / 1000 / 24);
							    
							}
							setAttr("day", day);
							renderJson(new String[] { "msg", "res", "day" });
							return;
						} else if (states.equals("21007")) {
							// 进入沙盒环境
							setting = false;
						} else {
							// 账单无效
							System.out.println("账单无效：" + states);
							// return Result.failure(states);
							renderJson("msg", "账单无效：" + states);
							return;
						}
						// 跟苹果验证有返回结果------------------
					}
					// 传上来的收据有购买信息==end=============
				}
			} else {
				System.out.println("已经验证过 receipt：" + receipt);
				// return Result.failure("已经验证过 receipt：\" + receipt");
				renderJson("msg", "已经验证过 receipt：" + receipt);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// return Result.failure(Constant.ERROR_01);
			renderJson("msg", "系统错误");
			return;
		}
	}
}
