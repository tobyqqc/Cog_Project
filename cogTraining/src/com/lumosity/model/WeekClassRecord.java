package com.lumosity.model;

import java.util.Date;
import java.util.List;

import com.lumosity.model.base.BaseWeekClassRecord;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class WeekClassRecord extends BaseWeekClassRecord<WeekClassRecord> {
	public static final WeekClassRecord dao = new WeekClassRecord();
	
	public WeekClassRecord findByIdAndDateAndClass(Long id, Date date, Integer classId) {
		return findFirst("select * from week_class_record where userId =? and weekEndDate =? and gameClassId =?", id, date, classId);
	}
	public List<WeekClassRecord> findByUserAndClass(Long id, Integer classId) {
		return find("select * from week_class_record where userId =? and gameClassId =?", id, classId);
	}
}
