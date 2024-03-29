package com.lumosity.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.lumosity.model.base.BasePlanGame;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class PlanGame extends BasePlanGame<PlanGame> {
	public static final PlanGame dao = new PlanGame();
	
	/**
	 * 以sequence的升序查询剩余的plangame(即isPlay=0)
	 * @param id 训练id
	 * @return
	 */
	public List<PlanGame> findRemaining(Integer id) {
		return find("select planGameId,trainPlanId,gameId,isPlay,sequence from plan_game where isPlay = 0 and trainPlanId =? order by sequence asc",id);
	}
	
	public PlanGame findByTrainPlanIdAndSequence(Integer id, Integer sequence) {
		return findFirst("select planGameId,trainPlanId,gameId,isPlay,sequence from plan_game where trainPlanId =? and sequence =?", id, sequence);
	}
	
	public List<PlanGame> findByTrainPlanId(Integer id) {
		return find("select planGameId,trainPlanId,gameId,isPlay,sequence from plan_game where trainPlanId =?", id);
	}
	
	public List<Record> findRecordByTrainPlanId(Integer id) {
		return Db.find("SELECT g.*,pg.`sequence`, pg.`isPlay` FROM plan_game pg LEFT JOIN game g ON pg.`gameId` = g.`gameId` WHERE pg.`trainPlanId` = ?", id);
	}
	
	public PlanGame findByTrainAndGame(Integer id, Integer gameId) {
		return findFirst("select planGameId,trainPlanId,gameId,isPlay,sequence from plan_game where trainPlanId = ? and gameId = ?", id, gameId);
	}
}
