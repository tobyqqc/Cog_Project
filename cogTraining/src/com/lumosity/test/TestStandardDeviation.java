package com.lumosity.test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class TestStandardDeviation {

	public static void main(String[] args) {
		//# old_mean 旧的平均值
		BigDecimal old_mean = new BigDecimal(29821.7002237);
		//# old_count 旧的数据量
		BigDecimal old_count = new BigDecimal(447); 
		//# new_value 新的值,当前游戏得到的分数
		BigDecimal new_value = new BigDecimal(30000); 
		//# old_std 旧的标准差
		BigDecimal old_std = new BigDecimal(12648.9918338);
		
		BigDecimal new_mean;
		BigDecimal square_sum;
		BigDecimal new_std;
		
		new_mean=(old_mean.multiply(old_count).add(new_value)).divide(old_count.add(new BigDecimal(1)), 10, BigDecimal.ROUND_CEILING);
		square_sum=old_std.multiply(old_std).multiply(old_count).subtract(new BigDecimal(3).multiply(old_mean).multiply(old_mean))
				.add(new BigDecimal(2).multiply(old_mean).multiply(old_mean).multiply(old_count));
		new_std= (square_sum.add(old_count.multiply(old_count)).add(new BigDecimal(4).multiply(new_mean).multiply(new_mean)).add(new_value.multiply(new_value)).subtract(new BigDecimal(2).multiply(new_value).multiply(new_mean)).subtract(new BigDecimal(2).multiply(old_mean).multiply(old_count).multiply(new_mean)))
				.divide(old_count.add(new BigDecimal(1)), 10, BigDecimal.ROUND_CEILING);
		
		MathContext mc = new MathContext(15, RoundingMode.HALF_DOWN);
		BigDecimal finalnum = new BigDecimal(Math.sqrt(new_std.doubleValue()) ,mc);
		
		System.out.println("new_std:"+ finalnum);
		System.out.println("new_mean:" + new_mean);
		//new_mean=(old_mean*old_count+new_value)/(old_count+1)
		//square_sum=old_std*old_std*old_count-3*old_mean*old_mean+2*old_mean*old_mean*old_count;
		//new_std=np.sqrt((square_sum+old_count*old_count+4*new_mean*new_mean+new_value*new_value-2*new_value*new_mean-2*old_mean*old_count*new_mean)/(old_count+1));
	
		
	}
}
