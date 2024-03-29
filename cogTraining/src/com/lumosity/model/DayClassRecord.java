package com.lumosity.model;

import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.lumosity.model.base.BaseDayClassRecord;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class DayClassRecord extends BaseDayClassRecord<DayClassRecord> {
	public static final DayClassRecord dao = new DayClassRecord();
	
	public DayClassRecord findByIdAndClassAndDate(Long id, Integer classId, Date date) {
		return findFirst("SELECT * FROM day_class_record WHERE userId =? AND gameClassId=? AND recordDate <= ? ORDER BY recordDate DESC", id, classId, date);
	}
	
	public DayClassRecord findByIdAndClassAndDate2(Long id, Integer classId, Date date) {
		return findFirst("SELECT * FROM day_class_record WHERE userId =? AND gameClassId=? AND recordDate = ? ORDER BY recordDate DESC", id, classId, date);
	}
	
	public Record findByIdAndClassAndDate(Long userId, Integer gameClassId, Date startDate, Date endDate) {
		return Db.findFirst("SELECT SUM(gameTimes) gameTimes FROM day_class_record WHERE userId =? AND gameClassId = ? AND recordDate <= ? AND recordDate >= ? GROUP BY gameClassId ",
				userId, gameClassId, endDate, startDate);
	}
	
	public List<DayClassRecord> findByIdAndDate(Long id, Date date) {
		return find("select * from day_class_record where userId =? and recordDate=?", id, date);
	}
	
	public List<DayClassRecord> findByIdAndLastRecord(Long id) {
		String sql = " SELECT * FROM day_class_record WHERE recordId " +
					 " IN  ( SELECT MAX(recordId) FROM day_class_record  WHERE userId = ? GROUP BY gameClassId)";
		return find(sql, id);
	}
}
