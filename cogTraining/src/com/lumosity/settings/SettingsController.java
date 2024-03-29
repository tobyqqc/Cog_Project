package com.lumosity.settings;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import com.alipay.api.domain.QRcode;
import com.swetake.util.Qrcode;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.lumosity.model.Account;
import com.lumosity.model.Order;
import com.lumosity.utils.DateTimeKit;
import org.json.JSONObject;

import javax.imageio.ImageIO;

public class SettingsController extends Controller{

	public void index() {
		
		Account account = getSessionAttr("userInfo");
		Record record = Order.dao.findTopupTrainClassByUserId(account.getUserId());
		
		StringBuffer topupStr = new StringBuffer();
//		for (Record record : topupList) {
//			topupStr
//			.append("【")
//			.append(record.getStr("gameClassName"))
//			.append("】")
//			.append("充值截止时间:")
//			.append(DateTimeKit.transferLongToDate("yyyy-MM-dd", record.getLong("expireTime")))
//			.append("<br>");
//		}
//		if(topupList == null || topupList.size() == 0) {
//			topupStr = topupStr.append("按年付费，享用付费时的全部功能");
//		}
		if (record == null || record.getLong("expireTime") == null) {
//			topupStr = topupStr.append("按年付费，享用付费时的全部功能");
			topupStr = topupStr.append("未充值");
		}
		else {
			topupStr
			.append("【")
			.append("认知训练")
			.append("】")
			.append("充值截止时间:")
			.append(DateTimeKit.transferLongToDate("yyyy-MM-dd", record.getLong("expireTime")))
			.append("<br>");
		}
		setAttr("topupInfo", topupStr.toString());
		setAttr("userId", account.getUserId());
		Account account2 = Account.dao.findById(account.getUserId());
		setAttr("isBindWeixin", account2.getWeixinId());
		setAttr("isBindQQ", account2.getQqId() == null ? "" : account2.getQqId());
		render("/settings/account_settings.html");
	}

	public void userInfoQR() {
		renderNull();
		Account account = getSessionAttr("userInfo");
		JSONObject obj = new JSONObject();
		obj.put("id", account.getUserId());
		obj.put("email", account.getEmail());
		obj.put("mobile", account.getMobileId());
		obj.put("cogdaily","cogdaily");


		try {
			//计算二维码图片的高宽比
			// API文档规定计算图片宽高的方式 ，v是本次测试的版本号
			int v =6;
			int width = 67 + 12 * (v - 1);
			int height = 67 + 12 * (v - 1);


			Qrcode x = new Qrcode();
			/**
			 * 纠错等级分为
			 * level L : 最大 7% 的错误能够被纠正；
			 * level M : 最大 15% 的错误能够被纠正；
			 * level Q : 最大 25% 的错误能够被纠正；
			 * level H : 最大 30% 的错误能够被纠正；
			 */
			x.setQrcodeErrorCorrect('L');
			x.setQrcodeEncodeMode('B');//注意版本信息 N代表数字 、A代表 a-z,A-Z、B代表 其他)
			x.setQrcodeVersion(v);//版本号  1-40
			String qrData =obj.toString();//内容信息

			byte[] d = qrData.getBytes("utf-8");//汉字转格式需要抛出异常

			//缓冲区
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);

			//绘图
			Graphics2D gs = bufferedImage.createGraphics();

			gs.setBackground(Color.WHITE);
			gs.setColor(Color.BLACK);
			gs.clearRect(0, 0, width, height);

			//偏移量
			int pixoff = 2;


			/**
			 * 容易踩坑的地方
			 * 1.注意for循环里面的i，j的顺序，
			 *   s[j][i]二维数组的j，i的顺序要与这个方法中的 gs.fillRect(j*3+pixoff,i*3+pixoff, 3, 3);
			 *   顺序匹配，否则会出现解析图片是一串数字
			 * 2.注意此判断if (d.length > 0 && d.length < 120)
			 *   是否会引起字符串长度大于120导致生成代码不执行，二维码空白
			 *   根据自己的字符串大小来设置此配置
			 */
			if (d.length > 0 && d.length < 120) {
				boolean[][] s = x.calQrcode(d);

				for (int i = 0; i < s.length; i++) {
					for (int j = 0; j < s.length; j++) {
						if (s[j][i]) {
							gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
						}
					}
				}
			}
			gs.dispose();
			bufferedImage.flush();
			//设置图片格式，与输出的路径
			ImageIO.write(bufferedImage, "png", getResponse().getOutputStream() );

		}
		catch (Exception e){
			e.printStackTrace();
		}
	}






}
