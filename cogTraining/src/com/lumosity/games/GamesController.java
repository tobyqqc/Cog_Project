package com.lumosity.games;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.lumosity.model.Account;
import com.lumosity.model.GameClass;
import com.lumosity.model.GameHistory;
import com.lumosity.model.Newbee;
import com.lumosity.model.Order;
import com.lumosity.model.PlanGame;
import com.lumosity.model.TrainPlan;
import com.lumosity.model.UserGame;
import com.lumosity.model.Game;
import com.lumosity.utils.CreateKit;
import com.lumosity.utils.DateTimeKit;
import com.lumosity.utils.LPIKit;



/**
 * 游戏列表的显示控制
 * 游戏界面渲染和结果的显示
 * @author Scott
 *
 */
@Before(FitTestInterceptor.class)
public class GamesController extends Controller{
	
	/**从数据库中查询游戏并列表的显示**/
	public void index() {
		Account account = getSessionAttr("userInfo");//获取用户
		String filter = getPara("filter");//获取过滤参数
		List<Record> gameClasses = new ArrayList<>();
		/*获取所有的游戏类型和游戏*/
		if (filter == null || "all".equals(filter)) {
			gameClasses = GameClass.dao.findAll2(account.getUserId());
			setAttr("filter", "all");
		} else {
			gameClasses = GameClass.dao.findByName2(filter);
			setAttr("filter", filter);
		}
		
		
		// 	
		boolean isLocked = true;
		List<Integer> gameIds = new ArrayList<>();
		
		List<PlanGame> planGames = new ArrayList<>();//游戏计划
		TrainPlan trainPlan = TrainPlan.dao.findByIdAndDate(account.getUserId(), DateTimeKit.getDate());
		if (trainPlan != null) {
			/*已经创建训练计划*/
			Integer id = trainPlan.getTrainPlanId();
			planGames = PlanGame.dao.findByTrainPlanId(id);
			if (planGames.size() <= 0) {
				/*该训练计划中没有游戏计划*/
				CreateKit.createPlanGame(planGames, id, account.getUserId());//创建游戏计划
			} 
		} else {
			//没有计划,则创建计划
			TrainPlan plan = new TrainPlan();
			plan.set("userId", account.getUserId())
				.set("playTimes", 0)
				.set("trainPlanDate", DateTimeKit.getDate())
				.set("week", DateTimeKit.getDateWeekNum())
				.save();
			//训练计划创建完成，则创建游戏计划
			planGames = new ArrayList<>();
			CreateKit.createPlanGame(planGames, plan.getTrainPlanId(), account.getUserId());
		}
		for (PlanGame planGame : planGames) {
			gameIds.add(planGame.getGameId());
		}
		// 去除最后两个游戏
		gameIds.remove(3);
		gameIds.remove(3);
		
		
		for (Record record : gameClasses) {
			//根据游戏类型查找游戏
			List<Record> hasGames = Game.dao.findByGameClassId2(record.getInt("gameClassId"));
			long gameClassExpireTime = Order.dao.findExpireTimeByUserIdAndGameClassId(account.getUserId(), record.getInt("gameClassId"));
			if (gameClassExpireTime > System.currentTimeMillis()) {
				isLocked = false;
			}
			
			for (Record game : hasGames) {
				if (isLocked && !gameIds.contains(game.getInt("gameId"))) {
					game.set("isLocked", true);
				}
				else {
					game.set("isLocked", false);
				}
//				game.set("isLocked", false);
			}
			
			if (hasGames.size() > 0) {
				//该类型有游戏，则该类型在页面显示
				record.set("games", hasGames);
			}
			isLocked = true;
		}
		setAttr("showClasses", gameClasses);
		render("/games/games.html");
	}
	/**单次游戏显示界面**/
	@Before(GameClientInterceptor.class)
	public void play() {
		Account account = getSessionAttr("userInfo");//获取用户
		Long userId = account.getUserId();//获取用户id
		String userName = account.getUserName();
		
		String name = getPara(0);
		Game game = Game.dao.findByName(name);
		int gameId = game.getGameId();
		String gameName = game.getGameName();
		//默认用户没有玩过游戏
		String jsonStr = "{\"fit_test\":\"1\",\"game_id\":\""+gameId+"\",\"game_param\":\""+gameName+"\",\"username\":\""+userName+"\",\"token\":\""
				+ "uqiwejhbaskjdbasdioqw\",\"game_user_setting\":{\"level\":\"1\",\"time\":\"5\",\"score\":\"0\"},\"updated_at\":\""
				+DateTimeKit.getPlayDate()+"\"}";
		//查找用户游戏关系表
		UserGame userGame = UserGame.dao.findByUserIdAndGameId(userId, gameId);
		if (userGame != null) {
			if (userGame.getLastPlayDate() != null && userGame.getLastPlayScore() != null) {
				//用户玩过游戏,fit_test:0
				jsonStr = "{\"fit_test\":\"0\",\"game_id\":\""+gameId+"\",\"game_param\":\""+gameName+"\",\"username\":\""+userName+"\",\"token\":\""
						+ "uqiwejhbaskjdbasdioqw\",\"game_user_setting\":{\"level\":\""+ (userGame.getLastLevel()==null ? 0 : userGame.getLastLevel()) +"\",\"time\":\"5\",\"score\":\""+userGame.getLastPlayScore()
						+"\"},\"updated_at\":\""+userGame.getLastPlayDate()+"\"}";	
			}	
		}
		System.out.println("jsonStr:" + jsonStr);
		setAttr("jsonStr", jsonStr);
		setAttr("game", game);
		render("/games/play.html");
	}
	
	/** 游戏数据的保存* */
	@Before(GameDataInterceptor.class)
	public void save() {
		Account account = getSessionAttr("userInfo");//获取用户
		Long userId = account.getUserId();//获取用户id
		
		int gameId = getParaToInt("gameId");
		String gameData = getPara("result");//获取异步提交的json字符串
		JSONObject jsonObject = new JSONObject(gameData);//使用json包解析json字符串
		int score = jsonObject.getInt("score");
		
		int LPI = getAttr("lpi");
		/*单次游戏的保存只需保存在gameHistory表中*/
		new GameHistory().set("gameId", gameId)
						.set("userId", userId)
						.set("score", score)
						.set("playDate", DateTimeKit.getDate())
						.set("gameLPI", LPI)
						.set("gameData", jsonObject.getString("trial_data"))
						.save();
		
//		int week = DateTimeKit.getDateWeekNum();
		
		setAttr("score", score);
		setAttr("LPI", LPI);
		setAttr("game", Game.dao.findById(gameId));
		List<GameHistory> histories = GameHistory.dao.findBestScore(userId, gameId);
//		if (histories.size() < 5) {
//			setAttr("msg", "游戏已完成");
//		} else {
//			setAttr("msg", "个人游戏前5名");
//		}
		setAttr("msg", "游戏已完成");
		setAttr("histories", histories);
//			renderJson();
		render("/games/result.html");
		
	}
}
