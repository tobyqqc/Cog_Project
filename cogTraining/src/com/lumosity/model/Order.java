package com.lumosity.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.lumosity.model.base.BaseOrder;
import com.lumosity.utils.DateTimeKit;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Order extends BaseOrder<Order> {
	public static final Order dao = new Order();

	public void saveOrder(String orderNo, Long userId, BigDecimal price, String payType, String gameClassIds,
			Integer gameDate, String trainClassOrderNo) {
		if (StringUtils.isNotBlank(gameClassIds)) {
			String[] gameClassIdss = gameClassIds.split(",");
			for (String gameClassId : gameClassIdss) {
				saveOrder(orderNo, userId, price, payType, Integer.parseInt(gameClassId), gameDate, trainClassOrderNo);
			}
		}
	}

	public void saveOrder(String orderNo, Long userId, BigDecimal price, String payType, Integer gameClassId,
			Integer gameDate, String trainClassOrderNo) {
		Order order = new Order();
		order.setOrderNo(orderNo);
		order.setUserId(userId);
		order.setPayTime(new Date());
		order.setPayPrice(price);
		order.setPayType(payType);
		order.setGameClassId(gameClassId);
		order.setTrainClassOrderNo(trainClassOrderNo);
//		Record gameDateDate = GameClass.dao.findDictDateById(gameDate);
//		String valueStr = gameDateDate.getStr("value");

		long expireTime = findExpireTimeByUserIdAndGameClassId(userId, gameClassId);
		expireTime = DateTimeKit.addDay(new Date(expireTime), (gameDate)).getTime();
		order.setExpireTime(expireTime);
		order.save();
	}



	/**
	 * 支付成功修改订单状态
	 * 
	 * @param orderNo
	 */
	public void successOrder(String orderNo) {
		List<Order> orders = find("select * from `order` where trainClassOrderNo = ?", orderNo);
		for (Order order : orders) {
			if (order.getStatus() == 0) {
				order.setStatus(1);
				order.update();
			}
		}
	}

	/**
	 * 根据用户ID和小类ID查询该小类的截止时间
	 * 
	 * @param userId
	 * @param gameClassId
	 * @return
	 */
	public long findExpireTimeByUserIdAndGameClassId(long userId, int gameClassId) {
		String sql = " SELECT MAX(o.`expireTime`) expireTime FROM "
				+ " `order` o WHERE o.`userId` = ? AND o.`gameClassId` = ? AND o.`status` = 1 ";
		Order order = findFirst(sql, userId, gameClassId);
		Long exprieTime = order == null ? null : order.getExpireTime();
		return exprieTime == null || exprieTime <  System.currentTimeMillis() ? System.currentTimeMillis() : exprieTime;
	}

	/**
	 * 根据用户ID和trainClassId查询该小类的截止时间
	 * 
	 * @param userId
	 * @param gameClassId
	 * @return
	 */
	public long findExpireTimeByUserIdAndTrainClassId(long userId, int trainClassId) {
		String sql = "SELECT MAX(o.`expireTime`) expireTime FROM `order` o WHERE o.`userId` = ? AND o.`status` = 1";
		Order order = findFirst(sql, userId);
		Long exprieTime = (order == null? null : order.getExpireTime());
		return exprieTime == null ? System.currentTimeMillis() : exprieTime;
	}
	
	/**
	 * 查询用户每个游戏小类的充值截止时间
	 * 
	 * @param userId
	 * @return
	 */
	public List<Record> findTopupGameClassByUserId(Long userId) {
		String sql = " SELECT MAX(o.`expireTime`) expireTime,gc.`gameClassId`,gc.`gameClassName` FROM `order` o "
				+ " LEFT JOIN game_class gc ON o.`gameClassId` = gc.`gameClassId` "
				+ " WHERE o.`userId` = ? and o.`status` = 1 " + " GROUP BY o.`gameClassId`";
		return Db.find(sql, userId);
	}
	
	/**
	 * 查询用户每个游戏小类的充值截止时间
	 * 
	 * @param userId
	 * @return
	 */
	public Record findTopupTrainClassByUserId(Long userId) {
		String sql = "SELECT MAX(o.`expireTime`) expireTime FROM `order` o WHERE o.`userId` = ? AND o.status = 1";
		return Db.findFirst(sql, userId);
	}

	public Page<Record> page(long userId, int pageNumber, int pageSize) {
		String sql = " SELECT o.`orderId`,o.`orderNo`,o.`payTime`,o.`payType`,o.`status`, o.`payPrice`, '认知训练' as gameClassName" +
					 " FROM `order` o " + 
					 " WHERE o.`userId` = " + userId +
					 " GROUP BY o.`trainClassOrderNo` " +
					 " ORDER BY o.`payTime` DESC ";
		return page(sql, pageNumber, pageSize);
	}

	public Page<Record> page(String sql, int pageNumber, int pageSize) {
		List<Object> outConditionValues = new ArrayList<Object>();
		if (pageNumber < 1 || pageSize < 1) {
			throw new ActiveRecordException("pageNumber and pageSize must more than 0");
		}

		String totalRowSql = sql;
		List result = Db.query(totalRowSql);
		int size = result.size();
		Boolean isGroupBySql = null;
		if (isGroupBySql == null) {
			isGroupBySql = size > 1;
		}

		long totalRow;
//		if (isGroupBySql) {
//			totalRow = size;
//		} else {
//			totalRow = (size > 0) ? ((Number) result.get(0)).longValue() : 0;
//		}
		totalRow = size;
		if (totalRow == 0) {
			return new Page<Record>(new ArrayList<Record>(0), pageNumber, pageSize, 0, 0);
		}

		int totalPage = (int) (totalRow / pageSize);
		if (totalRow % pageSize != 0) {
			totalPage++;
		}

		if (pageNumber > totalPage) {
			return new Page<Record>(new ArrayList<Record>(0), pageNumber, pageSize, totalPage, (int) totalRow);
		}
		// --------
		sql = forPaginate(pageNumber, pageSize, sql);
		System.out.println("sql:" + sql);
		List<Record> list = Db.find(sql, outConditionValues.toArray());
		return new Page<Record>(list, pageNumber, pageSize, totalPage, (int) totalRow);
	}
	
	 public String forPaginate(int pageNumber, int pageSize, String select) {
        int offset = pageSize * (pageNumber - 1);
        StringBuilder ret = new StringBuilder();
        ret.append(select);
        ret.append(" limit ").append(offset).append(", ").append(pageSize);	// limit can use one or two '?' to pass paras
        return ret.toString();
    }
	 
	 public boolean checkStatus(String orderNo) {
		 String sql = "select * from `order` where trainClassOrderNo = ? order by expireTime desc";
		 Order order = findFirst(sql, orderNo);
		 if (order == null || order.getStatus() == 0) {
			 System.out.println("order is null");
			 return false;
		 }
		 else {
			 System.out.println("order statis is :" + order.getStatus());
			 return true;
		 }
	 }

	/**
	 * 分配试用订单
	 * @param orderNo
	 * @param userId
	 * @param gameClassId
	 * @param gameDate
	 * @param trainClassOrderNo
	 * @return
	 */
	public boolean saveAllotOrder(String orderNo, Long userId, Integer gameClassId,
	Integer gameDate, String trainClassOrderNo){

		//分配 支付金额为0
		BigDecimal price=new BigDecimal(0);

		Order order = new Order();
		order.setOrderNo(orderNo);
		order.setUserId(userId);
		order.setPayTime(new Date());
		order.setPayPrice(price);
		order.setPayType("分配试用");
		order.setGameClassId(gameClassId);
		order.setTrainClassOrderNo(trainClassOrderNo);
		//支付状态设为成功
		order.setStatus(1);


		long expireTime = findExpireTimeByUserIdAndGameClassId(userId, gameClassId);
		expireTime = DateTimeKit.addDay(new Date(expireTime), (gameDate)).getTime();
		order.setExpireTime(expireTime);
		//out_expireTime=new Date(expireTime);
		return order.save();

	}
}
