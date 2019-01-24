package com.lumosity.model;

import java.util.Date;
import java.util.List;

import com.lumosity.model.base.BaseDayGameRecord;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class DayGameRecord extends BaseDayGameRecord<DayGameRecord> {
	public static final DayGameRecord dao = new DayGameRecord();
	
	public List<DayGameRecord> findByIdAndClassAndDate(Long id, Integer classId, Date date) {
		return find("select * from day_game_record where userId =? and gameClassId =? and recordDate =?", id, classId, date);
	}
	
	public DayGameRecord findByIdAndGameAndDate(Long id, Integer gameId, Date date) {
		return findFirst("select * from day_game_record where userId =? and gameId =? and recordDate =?", id, gameId, date);
	}
	
	public List<DayGameRecord> findByIdAndClass(Long id, Integer classId) {
		return find("select * from day_game_record where userId =? and gameClassId =?", id, classId);
	}
}