package com.lumosity.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseDayGameRecord<M extends BaseDayGameRecord<M>> extends Model<M> implements IBean {

	public void setRecordId(java.lang.Integer recordId) {
		set("recordId", recordId);
	}

	public java.lang.Integer getRecordId() {
		return get("recordId");
	}

	public void setUserId(java.lang.Long userId) {
		set("userId", userId);
	}

	public java.lang.Long getUserId() {
		return get("userId");
	}

	public void setGameId(java.lang.Integer gameId) {
		set("gameId", gameId);
	}

	public java.lang.Integer getGameId() {
		return get("gameId");
	}

	public void setScore(java.lang.Integer score) {
		set("score", score);
	}

	public java.lang.Integer getScore() {
		return get("score");
	}

	public void setLPI(java.lang.Integer LPI) {
		set("LPI", LPI);
	}

	public java.lang.Integer getLPI() {
		return get("LPI");
	}

	public void setGameClassId(java.lang.Integer gameClassId) {
		set("gameClassId", gameClassId);
	}

	public java.lang.Integer getGameClassId() {
		return get("gameClassId");
	}

	public void setGameTimes(java.lang.Integer gameTimes) {
		set("gameTimes", gameTimes);
	}

	public java.lang.Integer getGameTimes() {
		return get("gameTimes");
	}

	public void setRecordDate(java.util.Date recordDate) {
		set("recordDate", recordDate);
	}

	public java.util.Date getRecordDate() {
		return get("recordDate");
	}

}
