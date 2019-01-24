package com.lumosity.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lumosity.model.Game;
import com.lumosity.model.PlanGame;
/**
 * 首页游戏推荐算法（需要后期完善）
 * @author Scott
 *
 */
public class CreateKit {

	/**
	 * 创建游戏计划(需要后期算法完善！现阶段只是随机从游戏库中抽取5个游戏！)
	 * # 用户解锁游戏方案
     *   1. 根据推荐算法计算出来的五个游戏，这五个游戏推荐出来是有顺序的，首先把已经付费的能力的那几个游戏放到待选队列里
     *   2. 如果待选队列里游戏数量n小于3，那么按照推荐算法给出的顺序，从前往后，把剩下的没有付费的能力的游戏中的前3-n个游戏放入待选队列，则保证待选队列至少有三个
     *   3. 待选队列游戏作为当天解锁的游戏
	 * 
	 * @param games 游戏计划
	 * @param id	训练计划
	 * @return
	 */
	public static List<PlanGame> createPlanGame(List<PlanGame> games, Integer id, Long userId) {
		List<Integer> gameIdList = createGameId(userId);
		for (int i = 0; i < 5; i++) {
			Integer gameId = gameIdList.get(i);//取出随机游戏id
			PlanGame game = new PlanGame();
			game.set("trainPlanId", id)
				.set("gameId", gameId)
				.set("isPlay", 0)
				.set("sequence", i+1)
				.save();
			games.add(game);
		}
		return games;
	}
	
	/**
	 * 产生随机不重复的游戏id集合
	 * @return
	 */
	public static List<Integer> createGameId(Long userId) {
		
//		List<Integer> list = new ArrayList<>();//随机id集合
//		List<Game> games = Game.dao.findAll();//游戏库所有游戏
//		List<Integer> nums = new ArrayList<>();//所有游戏id集合
//		for (Game game : games) {
//			nums.add(game.getGameId());
//		}
//		
//		for (int i = 0; i < nums.size(); i++) {
//			Random random = new Random();
//			Integer number = nums.get(random.nextInt(nums.size()));//随机id
//			if (!list.contains(number)) {
//				list.add(number);//放入集合
//			}
//		}
		List<Integer> list = Game.dao.findGameIds(userId);
		List<Integer> gameIds = new ArrayList<>();
		for (int i=0; i < list.size(); i++) {
			Integer gameId = list.get(i);
			gameIds.add(++gameId);
		}
		for (Integer gameId : gameIds) {
			System.out.print(gameId + ",");
		}
		return gameIds;
		
	}
}
