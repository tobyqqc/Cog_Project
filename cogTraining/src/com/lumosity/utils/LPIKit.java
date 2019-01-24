package com.lumosity.utils;

import java.util.Date;
import java.util.List;

import com.lumosity.model.DayClassRecord;
import com.lumosity.model.DayGameRecord;

/**
 * LPI算法
 * @author Scott
 *
 */
public class LPIKit {
	
	private final static int a = 200;
	private final static int b = 1000;
	
	/**
	 * 游戏LPI初步算法
	 * @param score 游戏分数
	 * b = 1000
	 * a = 200
	 * @return
	 */
	public static int getLPI(int lastLpi, int score, float avg, float standard_deviation){
		System.out.println("lastLpi:" + lastLpi);
		System.out.println("score:" +score);
		System.out.println("avg:" + avg);
		System.out.println("standard_deviation:" + standard_deviation);
		// 游戏分数标准化公式
		int lpi = 0;
		float Z = ((score - avg) / standard_deviation);
		int T = (int) (Z * a +b);
		if (lastLpi == 0) {
			return T;
		}
		// 游戏分数缓动公公式
		if (T > lastLpi) {
			lpi = (int) ((lastLpi * 1.25 + T) / 2.25);
		}
		else if (T < lastLpi){
			lpi = (lastLpi * 4 + T) / 5;
		}
		else {
			lpi = lastLpi;
		}
		return lpi;
	}
	
	public static float[] standardDeviation(float[] arr) {
		float avg=0;
		float t_sum=0;
		float tt_sum=0;
		float standard_deviation = 0;
		for (int i = 0; i < arr.length; i++) {
			t_sum+=arr[i];
		}
		avg=t_sum/arr.length;
		for (int i = 0; i < arr.length; i++) {
			tt_sum+=(arr[i]-avg)*(arr[i]-avg);
		}
		standard_deviation = (float) Math.sqrt(tt_sum/arr.length);
		return new float[] {avg, standard_deviation};
	}
	
	/**
	 * 获取游戏类型 的LPI,初步算法即取出该游戏类型下所有游戏的日LPI，然后求平均数
	 * @param id 用户id
	 * @param classId 游戏类型id
	 * @return
	 */
	public static int getClassLPI(Long id, int classId, Date date){
		List<DayGameRecord> records = DayGameRecord.dao.findByIdAndClassAndDate(id, classId, date);
		//事实上，records必定大于0，因为它是在个人游戏档案表更新后执行的
		if (records.size() > 0) {
			int sum = 0;
			for (DayGameRecord dayRecord : records) {
				sum += dayRecord.getLPI();
			}
			return sum/records.size();
		} else {
			return 0;
		}
	}
	/**
	 * 获取用户当天的总LPI（currentLPI），初步算法：取出当天的个类型的LPI，然后求平均数
	 * @param id 用户id
	 * @param date 时间
	 * @return
	 */
	public static int getCurrentLPI(Long id, Date date, int classLPI, Integer gameClassId){
		List<DayClassRecord> records = DayClassRecord.dao.findByIdAndLastRecord(id);
		if (records != null && records.size() == 5) {
			int sum = 0;
			sum += classLPI;
			for (DayClassRecord record : records) {
				if (record.getGameClassId() != gameClassId) {
					sum += record.getLPI();
				}
			}
			return sum / (records.size());
		}
		else {
			return 0;
		}
		
	}
}
