package com.lumosity.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import com.lumosity.model.Newbee;

public class StandardDeviationKit {

	public static Newbee getSd(Newbee newbee, int score) {
		
		//# old_mean 旧的平均值
		BigDecimal old_mean = newbee.getMean();
		//# old_count 旧的数据量
		BigDecimal old_count = new BigDecimal(newbee.getCount()); 
		//# new_value 新的值,当前游戏得到的分数
		BigDecimal new_value = new BigDecimal(score); 
		//# old_std 旧的标准差
		BigDecimal old_std = newbee.getStd();
		
		BigDecimal new_mean;
		BigDecimal square_sum;
		BigDecimal new_std;
		
		new_mean=(old_mean.multiply(old_count).add(new_value)).divide(old_count.add(new BigDecimal(1)), 10, BigDecimal.ROUND_CEILING);
		square_sum=old_std.multiply(old_std).multiply(old_count).subtract(new BigDecimal(3).multiply(old_mean).multiply(old_mean))
				.add(new BigDecimal(2).multiply(old_mean).multiply(old_mean).multiply(old_count));
		new_std= (square_sum.add(old_count.multiply(old_count)).add(new BigDecimal(4).multiply(new_mean).multiply(new_mean)).add(new_value.multiply(new_value)).subtract(new BigDecimal(2).multiply(new_value).multiply(new_mean)).subtract(new BigDecimal(2).multiply(old_mean).multiply(old_count).multiply(new_mean)))
				.divide(old_count.add(new BigDecimal(1)), 10, BigDecimal.ROUND_CEILING);
		
		MathContext mc = new MathContext(15, RoundingMode.HALF_DOWN);
		new_std = new BigDecimal(Math.sqrt(new_std.doubleValue()) ,mc);
		
//		System.out.println("new_std:"+ new_std);
//		System.out.println("new_mean:" + new_mean);
		
		newbee.setCount(old_count.intValue()+1);
		newbee.setMean(new_mean);
		newbee.setStd(new_std);
		return newbee;
	}
	
	public static void main(String[] args) {
		Newbee newbee = new Newbee();
		newbee.setMean(new BigDecimal(29821.7002237));
		newbee.setStd(new BigDecimal(12648.9918338));
		newbee.setCount(447);
		Newbee newbee2 = StandardDeviationKit.getSd(newbee, 30000);
		System.out.println(newbee2.getMean().floatValue());
		System.out.println(newbee2.getStd().floatValue());
		
	}
}
