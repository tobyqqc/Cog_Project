package com.lumosity.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.lumosity.games.GameDataInterceptor;
import com.lumosity.model.Account;
import com.lumosity.model.GameHistory;
import com.lumosity.model.OauthUser;
import com.lumosity.model.Order;
import com.lumosity.model.Game;
import com.lumosity.model.GameClass;
import com.lumosity.model.PlanGame;
import com.lumosity.model.QuestionStatus;
import com.lumosity.model.TestPlan;
import com.lumosity.model.TrainPlan;
import com.lumosity.model.UserGame;
import com.lumosity.utils.*;


/**
 * 接口API controllerKey = /api/auth
 * @author Scott
 * @since 2017.05.17
 */
@Clear
public class AuthApiController extends Controller {
	
	public static final String emailReg = "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
	public static final String phoneReg = "^1[3|4|5|7|8][0-9]{9}$";

	/**
	 * 注册功能
	 */
//	@ActionKey("/api/auth/doSignUp")
	public void doSignUp() {
		
		String accountInfo = getPara("accountInfo");
		Account account = Account.dao.isAccountExist(accountInfo);
		Account loginInfo = null;
		if (account != null) {
			//注册账号已存在，返回错误信息
			renderJson("msg", "邮箱或手机号已被注册！！");
		} else {
			Date birth = getParaToDate("date");
			String userName = getPara("userName");
			String password = getPara("password");
			
			if (Pattern.matches(emailReg, accountInfo)) {
				//邮箱
				new Account().set("userName", userName).set("email", accountInfo).set("createDate", DateTimeKit.getDate())
					.set("password", password).set("birthday", birth).save();
				loginInfo = Account.dao.findByEmailAndPwd(accountInfo, password);
			} else if (Pattern.matches(phoneReg, accountInfo)) {
				//手机
				new Account().set("userName", userName).set("mobileId", accountInfo).set("createDate", DateTimeKit.getDate())
					.set("password", password).set("birthday", birth).save();
				loginInfo = Account.dao.findByPhoneAndPwd(accountInfo, password);
			} else {
				//非法输入
				renderJson("msg", "你输入的邮箱或者手机号非法！请重新输入");
				return;
			}
			
			Long userId = loginInfo.getUserId();
			String email = loginInfo.getEmail();
			Integer gender = loginInfo.getGender();
			Integer edu = loginInfo.getEducationId();
			Integer job = loginInfo.getJobId();
			Date create = loginInfo.getCreateDate();
			String phone = loginInfo.getMobileId();
			String weixin = loginInfo.getWeixinId();
			
			if (birth == null) 
				birth = new Date();
			if (gender == null) 
				gender = 0;
			if (edu == null) 
				edu = 0;
			if (job == null) 
				job = 0;
			if (create == null) 
				create = new Date();
			if (phone == null) 
				phone = "";
			if (weixin == null) 
				weixin = "";
			if (email == null) 
				email = "";
			Map<String, Object> returnInfo = new HashMap<>();
			returnInfo.put("userId", userId);
			returnInfo.put("userName", userName);
			returnInfo.put("email", email);
			returnInfo.put("password", password);
			returnInfo.put("birthday", birth);
			returnInfo.put("gender", gender);
			returnInfo.put("educationId", edu);
			returnInfo.put("jobId", job);
			returnInfo.put("createDate", create);
			returnInfo.put("mobileId", phone);
			returnInfo.put("weixinId", weixin);
			returnInfo.put("isMember", 0);
			setAttr("account", returnInfo);
			
			//创建适应性训练计划(固定为三种类型游戏)
			new TestPlan().set("userId", userId).set("gameId", 14).set("sequence", 1).set("isPlay", 0).save();//speed_match
			new TestPlan().set("userId", userId).set("gameId", 7).set("sequence", 2).set("isPlay", 0).save();//lost_in_migration
			new TestPlan().set("userId", userId).set("gameId", 13).set("sequence", 3).set("isPlay", 0).save();//memory_matrix
			//创建用户问卷状态
			new QuestionStatus().set("userId", userId).set("question1Status", 0).set("question2Status", 0).set("survey", "").save();
			
			
			setAttr("question1Status", 0);
			setAttr("question2Status", 0);
			setAttr("msg", "success");
			
			renderJson(new String[] {"msg","question1Status","question2Status","account"});
		}
		
	}
	/**
	 * 登录功能
	 */
	@ActionKey("/api/auth/doLogin")
	public void doLogin() {
		
		String accountInfo = getPara("accountInfo");
		String pwd = getPara("password");
		
		Account account = Account.dao.findAccount(accountInfo, pwd);
		Map<String, Object> loginInfo = new HashMap<>();
		
		if (account != null) {
			Date birth = account.getBirthday();
			String email = account.getEmail();
			Integer gender = account.getGender();
			Integer edu = account.getEducationId();
			Integer job = account.getJobId();
			Date create = account.getCreateDate();
			String phone = account.getMobileId();
			String weixin = account.getWeixinId();
			String qqId = account.getQqId();
			
			if (birth == null) 
				birth = new Date();
			if (gender == null) 
				gender = 0;
			if (edu == null) 
				edu = 0;
			if (job == null) 
				job = 0;
			if (create == null) 
				create = new Date();
			if (phone == null) 
				phone = "";
			if (weixin == null) 
				weixin = "";
			if (email == null) 
				email = "";
			if (qqId == null) {
				qqId = "";
			}
			// 是否完成适应性训练 0:未完成  1:已完成
			int testPlanStatus = 0; 
			List<TestPlan> testPlans = TestPlan.dao.remaining(account.getUserId());
			if (testPlans == null || testPlans.size() == 0) {
				testPlanStatus = 1;
			}
			loginInfo.put("userId", account.getUserId());
			loginInfo.put("userName", account.getUserName());
			loginInfo.put("email", email);
			loginInfo.put("password", pwd);
			loginInfo.put("birthday", birth);
			loginInfo.put("gender", gender);
			loginInfo.put("educationId", edu);
			loginInfo.put("jobId", job);
			loginInfo.put("createDate", create);
			loginInfo.put("mobileId", phone);
			loginInfo.put("weixinId", weixin);
			loginInfo.put("qqId", qqId);
			loginInfo.put("isMember", 0);
			loginInfo.put("testPlanStatus", testPlanStatus);
			setAttr("msg", "success");
			setAttr("account", loginInfo);
			
			QuestionStatus status = QuestionStatus.dao.findById(account.getUserId());
			setAttr("questionStatus1", status.getQuestion1Status());
			setAttr("questionStatus2", status.getQuestion2Status());
			
			renderJson(new String[] {"msg", "account","questionStatus1","questionStatus2"});
		} else {
			renderJson("msg", "账号或密码错误！");
		}
	}
	/**
	 * 找回密码，校验下2次输入是否相同，一致直update
	 */
//	@ActionKey("/api/auth/doResetPassword")
	public void doResetPassword() {
		
		String accountInfo = getPara("accountInfo");
		Account account = Account.dao.isAccountExist(accountInfo);
		String password = getPara("password");
		if (password == null || "".equals(password.trim())) {
			renderJson("msg", "不能为空");
		} else {
			account.set("password", password).update();
			renderJson("msg", "success");
		}
	}
	
	/**
	 * 修改个人信息
	 */
//	@ActionKey("/pi/auth/doUpdatePersonalInfo")
	public void doUpdatePersonalInfo(){
		
		Long userId = getParaToLong("userId");
		Account account = Account.dao.findById(userId);
		String email = getPara("email");
		String phone = getPara("phone");
		String name = getPara("name");
		Date birth = getParaToDate("date");
		Integer gender = getParaToInt("gender");
		Integer education = getParaToInt("education");
		Integer job = getParaToInt("job");
		
		if (account != null) {
			if (email != null) {
				Account valiAccount = Account.dao.findByEmail(email);
				if (valiAccount != null) {
					//修改邮箱已经被占用,不允许修改,跳转修改页面
					renderJson(InvokeResult.failure("邮箱已占用"));
					return;
				} 
				else {
					account.set("email", email).update();
				}
			}
			if (phone != null) {
				Account valiAccount = Account.dao.findByPhone(phone);
				if (valiAccount!= null) {
					renderJson(InvokeResult.failure("手机号已占用"));
					return;
				}
				else {
					account.set("mobileId", phone).update();
				}
			}
			if (name != null) 
				account.set("userName", name).update();
			if (birth != null) 
				account.set("birthday", birth).update();
			if (gender != null) 
				account.set("gender", gender).update();
			if (education != null) 
				account.set("educationId", education).update();
			if (job != null) 
				account.set("jobId", job).update();
			renderJson(InvokeResult.success());
		} else {
			renderJson(InvokeResult.failure("修改失败"));
		}
	}
	
	/**
	 * 问卷保存
	 */
	@ActionKey("/api/auth/saveFirQuestion")
	public void saveFirQuestion(){
		
		Long userId = getParaToLong("userId");
		Account account = Account.dao.findById(userId);
		account.set("gender", getParaToInt("gender")).set("educationId", getParaToInt("education"))
			.set("jobId", getParaToInt("job")).update();
		QuestionStatus status = QuestionStatus.dao.findById(userId);
		if (status != null)
			status.set("question1Status", 1).update();
		else
			new QuestionStatus().set("userId", userId).set("question1Status", 1).save();
		renderJson("msg", "success");
	}
	
	/**
	 * 问卷保存
	 */
	@ActionKey("/api/auth/saveSecQuestion")
	public void saveSecQuestion(){
		
		Long userId = getParaToLong("userId");
		String result = getPara("result");
//		Account account = Account.dao.findById(userId);
		
		QuestionStatus status = QuestionStatus.dao.findById(userId);
		if (status != null)
			status.set("question2Status", 1).set("survey", result).update();
		else
			new QuestionStatus().set("userId", userId).set("question1Status", 1).set("question2Status", 1).set("survey", result).save();
		renderJson("msg", "success");
		
	}
	/**
	 * 获取所有游戏
	 */
	@ActionKey("/api/game/games")
	public void games() {
		
		Long userId = getParaToLong("userId");
		Account account = Account.dao.findById(userId);
		String userName = account.getUserName();
		
		List<Object> games = new ArrayList<>();
		List<GameClass> gameClasses = GameClass.dao.findAll();
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
		
		for (GameClass gameClass : gameClasses) {
			long gameClassExpireTime = Order.dao.findExpireTimeByUserIdAndGameClassId(userId, gameClass.getGameClassId());
			List<Game> gameList = Game.dao.findByGameClassId(gameClass.getGameClassId());
			for (int i=0; i < gameList.size(); i++) {
				Game game = gameList.get(i);
				String gameName = game.getGameName();
				int gameId = game.getGameId();
				Integer isLike = 0;
				String jsonStr = "{'game':'"+game.getPathName()+"','datas':{'fit_test':'1','game_id':'"+gameId+"','game_param':'"+gameName+"','username':'"+userName+"','token':'"
							+ "uqiwejhbaskjdbasdioqw','game_user_setting':{'level':'1','time':'5','score':'0'},'updated_at':'"
							+DateTimeKit.getPlayDate()+"'}}";
				UserGame userGame = UserGame.dao.findByUserIdAndGameId(userId, gameId);
				if (userGame != null) {
					if (userGame.getLastPlayDate() != null && userGame.getLastPlayScore() != null) {
						jsonStr = "{'game':'"+game.getPathName()+"','datas':{'fit_test':'0','game_id':'"+gameId+"','game_param':'"+gameName+"','username':'"+userName+"','token':'"
								+ "uqiwejhbaskjdbasdioqw','game_user_setting':{'level':'"+ 
								(userGame.getLastLevel()==null ? 1 : userGame.getLastLevel())
								+"','time':'5','score':'"+userGame.getLastPlayScore()+"'},'updated_at':'"
								+userGame.getLastPlayDate()+"'}}";
					}
					isLike = userGame.getIsLike();
				}
				
				Map<String, Object> gameMap = new HashMap<>();
				List<GameHistory> histories = GameHistory.dao.findBestScore(userId, gameId);
				if (histories.size() > 0) {
					gameMap.put("bestScore", histories.get(0).getScore());
				} else {
					gameMap.put("bestScore", 0);
				}
				String imgPath = "http://120.27.234.232/resources/images/games/" + game.getPadGameImg();
				int gamePlayTimes = 0;
				if ((userGame != null) && (userGame.getPlayTimes() != null)) {
					gamePlayTimes = userGame.getPlayTimes().intValue();
				}
				
				if (gameClassExpireTime <= System.currentTimeMillis() && !gameIds.contains(game.getInt("gameId"))) {
					gameMap.put("isLocked", 1);
				}
				else {
					gameMap.put("isLocked", 0);
				}
				gameMap.put("imgPath", imgPath);
				gameMap.put("gameOpenCode", jsonStr);
				gameMap.put("gameId", gameId);
				gameMap.put("gameName", gameName);
				gameMap.put("brief", game.getBrief());
				gameMap.put("benefit", game.getBenefit());
				gameMap.put("gameFileName", game.getGameFileName());
				gameMap.put("gamePathName", game.getPathName());
				gameMap.put("gameClassId", game.getGameClassId());
				gameMap.put("isLike", isLike);
				
				gameMap.put("gamePlayTimes", gamePlayTimes);
				games.add(gameMap);
				
			}
		}
		System.out.println(JsonKit.toJson(games));
		renderJson(games);
		
	}
	
	/**
	 * 用户游戏交互接口
	 */
	@ActionKey("/api/game/userGame")
	public void userGame() {
		
		Long userId = getParaToLong("userId");
		Integer gameId = getParaToInt("gameId");
		
		List<GameHistory> histories = GameHistory.dao.findBestScore(userId, gameId);
		UserGame userGame = UserGame.dao.findByUserIdAndGameId(userId, gameId);
		int gamePlayTimes = 0;
		int bestScore = 0;
		if (histories.size() > 0) 
			bestScore = histories.get(0).getScore();
		if (userGame != null)
			if (userGame.getPlayTimes() != null)//主要是兼容之前的版本，增强是同容错性
				gamePlayTimes = userGame.getPlayTimes();
		setAttr("bestScore", bestScore);
		setAttr("gamePlayTimes", gamePlayTimes);
		renderJson(new String[] {"bestScore","gamePlayTimes"});
		
	}
	
	/**
	 * 获取用户游戏情况
	 */
	@ActionKey("/api/game/updateGameFavi")
	public void updateGameFavi() {
		
		Long userId = getParaToLong("userId");
		Integer gameId = getParaToInt("gameId");
		Integer isLike = getParaToInt("isLike", 0);
		
		UserGame userGame = UserGame.dao.findByUserIdAndGameId(userId, gameId);
		
		if (userGame != null) {
			userGame.set("isLike", isLike).update();
		} else {
			userGame = new UserGame().set("userId", userId).set("gameId", gameId).set("isLike", isLike);
			userGame.save();
		}
		setAttr("msg", "更新成功！");
		setAttr("isLike", userGame.getIsLike());
		renderJson(new String[] {"msg","isLike"});
	}
	
	/**
	 * 获取用户游戏关系
	 */
	@ActionKey("/api/game/getGameFavi")
	public void getGameFavi() {
		
		Long userId = getParaToLong("userId");
		Integer gameId = getParaToInt("gameId");
		
		UserGame userGame = UserGame.dao.findByUserIdAndGameId(userId, gameId);
		if (userGame != null) 
			renderJson(userGame.getIsLike());
		else 
			renderJson(0);
	}
	
	/**
	 * 拼接游戏开始json字符串
	 */
	@ActionKey("/api/game/startGame")
	public void startGame(){
		
		Long userId = getParaToLong("userId");
		Integer gameId = getParaToInt("gameId");
		
		Game game = Game.dao.findById(gameId);
		String gameName = game.getGameName();
		String userName = Account.dao.findById(userId).getUserName();
		// fit_test:是否显示教程 
		String jsonStr = "{'game':'"+game.getPathName()+"','datas':{'fit_test':'1','game_id':'"+gameId+"','game_param':'"+gameName+"','username':'"+userName+"','token':'"
				+ "uqiwejhbaskjdbasdioqw','game_user_setting':{'level':'1','time':'5','score':'0'},'updated_at':'"
				+DateTimeKit.getPlayDate()+"'}}";
		UserGame userGame = UserGame.dao.findByUserIdAndGameId(userId, gameId);
		if (userGame != null) {
			if (userGame.getLastPlayDate() != null && userGame.getLastPlayScore() != null) {
				jsonStr = "{'game':'"+game.getPathName()+"','datas':{'fit_test':'0','game_id':'"+gameId+"','game_param':'"+gameName+"','username':'"+userName+"','token':'"
						+ "uqiwejhbaskjdbasdioqw','game_user_setting':{'level':'"+
						(userGame.getLastLevel()==null ? 1 : userGame.getLastLevel())
						+"','time':'5','score':'"+userGame.getLastPlayScore()+"'},'updated_at':'"
						+userGame.getLastPlayDate()+"'}}";
			}
		}
		Map<String, String> result = new HashMap<>();
		result.put("gameOpenCode", jsonStr);
		renderJson(result);
		/*
		 * {"fit_test":"1","game_id":"1","game_param":"速度匹配","username":"test",
		 * "token":"uqiwejhbaskjdbasdioqw","game_user_setting":{"level":"3","time":"5","score":"1230"},
		 * "updated_at":"201701011228"}
		 */
	}

	/**
	 * 单次游戏保存游戏数据
	 */
	@ActionKey("/api/game/postGameData")
	@Before(GameDataInterceptor.class)
	public void postGameData() {
		
		Long userId = getParaToLong("userId");
		int gameId = getParaToInt("gameId");
		
		String gameData = getPara("result");
		JSONObject jsonObject = new JSONObject(gameData);
		int score = jsonObject.getInt("score");

		int LPI = getAttr("lpi");
		//单次游戏的保存只需保存在gameHistory表中
		new GameHistory().set("gameId", gameId).set("userId", userId)
						.set("score", score).set("playDate", DateTimeKit.getDate())
						.set("gameLPI", LPI).set("gameData", jsonObject.getString("trial_data"))
						.save();
		//查询游戏前5名
		List<GameHistory> histories = GameHistory.dao.findBestScore(userId, gameId);
		
		setAttr("score", score);
		setAttr("BrainAI", LPI);
		setAttr("histories", histories);
		renderJson(new String[] {"score","histories","BrainAI"});
		
	}
	/**
	 * 单次游戏保存游戏数据
	 */
	@ActionKey("/api/game/postTestGameData")
	public void postTestGameData() {
		
		Long userId = getParaToLong("userId");
		Integer gameId = getParaToInt("gameId");
		String gameData = getPara("result");//获取游戏传入数据
		
		List<TestPlan> testPlans = TestPlan.dao.remaining(userId);
		if (testPlans.size() > 0) {
			TestPlan testPlan = testPlans .get(0);
			if (gameData.trim() != null && gameData.length() != 0) {
				//游戏数据为空，代表的是用户主动结束游戏
				JSONObject jsonObject = new JSONObject(gameData);//转为java json对象
				int score = jsonObject.getInt("score");
				
				// 获取上次游戏分数
				Integer lastScore = GameHistory.dao.findLastScoreByUserId(gameId, userId);
				// 获取该游戏的所有分数 
				float[] arr = GameHistory.dao.findScoreByGameId(gameId);
				float[] params = LPIKit.standardDeviation(arr);
				
				int LPI = LPIKit.getLPI(lastScore, score, params[0], params[1]);
				/*保存本次游戏数据*/
//				new GameHistory().set("gameId", gameId)
//								.set("userId", userId)
//								.set("score", score)
//								.set("playDate", DateTimeKit.getDate())
//								.set("gameLPI", LPI)
//								.set("gameData", jsonObject.getString("trial_data"))
//								.save();
//				//更新testPlan状态
//				testPlan.set("isPlay", 1).update();
				testPlan.saveGameDate(score, LPI, jsonObject.getString("trial_data"));
				setAttr("msg", "保存成功");
				setAttr("score", score);
				setAttr("BrainAI", LPI);
				setAttr("sequence", testPlan.getSequence());
				renderJson(new String[] {"msg","score","sequence","BrainAI"});
			} else {
				renderJson("msg", "保存失败");
			}
		} else {
			renderJson("msg", "你已完成适应性训练计划！请勿再次提交数据！");
		}
		
		
		
	}
	/**
	 * 训练计划游戏保存游戏数据
	 */
	@ActionKey("/api/game/postTrainGameData")
	@Before(GameDataInterceptor.class)
	public void postTrainGameData() {
		
		Long userId = getParaToLong("userId");
		Integer gameId = getParaToInt("gameId");
		//获取用户今日训练计划
		TrainPlan trainPlan = TrainPlan.dao.findByIdAndDate(userId, DateTimeKit.getDate());
		//获取用户今日训练计划id
		Integer trainPlanId = trainPlan.getTrainPlanId();
		//获取用户今日剩余游戏计划(以sequence升序排列)
//		List<PlanGame> planGames = PlanGame.dao.findRemaining(trainPlanId);
		//获取本次游戏
		PlanGame planGame = PlanGame.dao.findByTrainAndGame(trainPlanId, gameId);
		//保存本次游戏
		if (planGame != null) {
			String gameData = getPara("result");
			JSONObject jsonObject = new JSONObject(gameData);
			int score = jsonObject.getInt("score");
			
			int LPI = getAttr("lpi");
			//单次游戏的保存只需保存在gameHistory表中
			new GameHistory().set("gameId", gameId).set("userId", userId)
							.set("score", score).set("playDate", DateTimeKit.getDate())
							.set("gameLPI", LPI).set("gameData", jsonObject.getString("trial_data"))
							.save();
			//设置该游戏状态为已完成
			planGame.set("isPlay", 1).update();
			//设置今日计划的完成状态
			trainPlan.set("playTimes", trainPlan.getPlayTimes()+1).update();
			//查询游戏前5名
			List<GameHistory> histories = GameHistory.dao.findBestScore(userId, gameId);
			setAttr("score", score);
			setAttr("BrainAI", LPI);
			setAttr("histories", histories);
			setAttr("sequence", planGame.getSequence());
			
			renderJson(new String[] {"score","histories","sequence","BrainAI"});
		} else {
			renderJson("msg", "今日训练计划已完成，请勿提交！");
		}
		
		
	}
	/**
	 * 获取训练计划中游戏，需要传入用户id
	 * 返回几个计划游戏的id
	 */
	@ActionKey("/api/game/planGame")
	public void planGame() {
		
		Long userId = getParaToLong("userId");
		if ("".equals(userId) || userId == null) {
			renderJson("msg", "invali userId");
			return;
		}

		List<Object> games = new ArrayList<>();
		List<TestPlan> testPlans = TestPlan.dao.remaining(userId);
		if (testPlans.size() > 0){
			//适应性训练没完成
			setAttr("isTestPlanEnd", 0);
			testPlans = TestPlan.dao.findByUser(userId);
			int isLike = 0;
			for (TestPlan testPlan : testPlans) {
				Map<String, Object> gameMap = new HashMap<>();
				
				Game game = Game.dao.findById(testPlan.getGameId());
				int gameId = game.getGameId();
				UserGame userGame = UserGame.dao.findByUserIdAndGameId(userId, gameId);
				String imgPath = "http://120.27.234.232/resources/images/games/" + game.getPadGameImg();
				
				String gameName = game.getGameName();
				String userName = Account.dao.findById(userId).getUserName();
				String jsonStr = "{'game':'"+game.getPathName()+"','datas':{'fit_test':'1','game_id':'"+gameId+"','game_param':'"+gameName+"','username':'"+userName+"','token':'"
						+ "uqiwejhbaskjdbasdioqw','game_user_setting':{'level':'1','time':'5','score':'0'},'updated_at':'"
						+DateTimeKit.getPlayDate()+"'}}";
				
				if (userGame != null) {
					isLike = userGame.getIsLike();
				}
				
				gameMap.put("imgPath", imgPath);
				gameMap.put("gameOpenCode", jsonStr);
				gameMap.put("isPlay", testPlan.getIsPlay());
				gameMap.put("sequence", testPlan.getSequence());
				gameMap.put("gameId", gameId);
				gameMap.put("gameClassId", game.getGameClassId());
				gameMap.put("gameName", gameName);
				gameMap.put("brief", game.getBrief());
				gameMap.put("benefit", game.getBenefit());
				gameMap.put("gameFileName", game.getGameFileName());
				gameMap.put("isLike", isLike);
				
				games.add(gameMap);
				
			}
			setAttr("games", games);
			renderJson(new String[] {"isTestPlanEnd","games"});
			return;
		}
		List<PlanGame> planGames = new ArrayList<>();
		TrainPlan trainPlan = TrainPlan.dao.findByIdAndDate(userId, DateTimeKit.getDate());
		if (trainPlan != null) {
			//已经创建训练计划
			Integer id = trainPlan.getTrainPlanId();
			planGames = PlanGame.dao.findByTrainPlanId(id);
			//该训练计划中没有游戏计划，创建游戏计划
			if (planGames == null || planGames.size() == 0) 
				CreateKit.createPlanGame(planGames, id, userId);
			if (trainPlan.getPlayTimes() > 0 ) 
				setAttr("isBegin", 1);
			else 
				setAttr("isBegin", 0);
		} else {
			//没有计划,则创建计划
			new TrainPlan().set("userId", userId)
				.set("playTimes", 0)
				.set("trainPlanDate", DateTimeKit.getDate())
				.set("week", DateTimeKit.getDateWeekNum())
				.save();
			trainPlan = TrainPlan.dao.findByIdAndDate(userId, DateTimeKit.getDate());
			//训练计划创建完成，则创建游戏计划
			CreateKit.createPlanGame(planGames, trainPlan.getTrainPlanId(), userId);
			setAttr("isBegin", 0);
		}
		
		int i = 0;
//		long expireTime = Order.dao.findExpireTimeByUserIdAndTrainClassId(userId, 1);
		for (PlanGame planGame : planGames) {
			Map<String, Object> gameMap = new HashMap<>();
			
			Game game = Game.dao.findById(planGame.getGameId());
			int gameId = game.getGameId();
			String imgPath = "http://120.27.234.232/resources/images/games/" + game.getPadGameImg();
			
			String gameName = game.getGameName();
			String userName = Account.dao.findById(userId).getUserName();
			String jsonStr = "{'game':'"+game.getPathName()+"','datas':{'fit_test':'1','game_id':'"+gameId+"','game_param':'"+gameName+"','username':'"+userName+"','token':'"
					+ "uqiwejhbaskjdbasdioqw','game_user_setting':{'level':'1','time':'5','score':'0'},'updated_at':'"
					+DateTimeKit.getPlayDate()+"'}}";
			UserGame userGame = UserGame.dao.findByUserIdAndGameId(userId, gameId);
			if (userGame != null) {
				if (userGame.getLastPlayDate() != null && userGame.getLastPlayScore() != null) {
					jsonStr = "{'game':'"+game.getPathName()+"','datas':{'fit_test':'0','game_id':'"+gameId+"','game_param':'"+gameName+"','username':'"+userName+"','token':'"
							+ "uqiwejhbaskjdbasdioqw','game_user_setting':{'level':'"+
							(userGame.getLastLevel()==null ? 1 : userGame.getLastLevel()) +"'"
									+ ",'time':'5','score':'"+userGame.getLastPlayScore()+"'},'updated_at':'"
							+userGame.getLastPlayDate()+"'}}";
				}	
			}
			long gameClassExpireTime = Order.dao.findExpireTimeByUserIdAndGameClassId(userId, game.getGameClassId());
			if (i >= 3 && (gameClassExpireTime <= System.currentTimeMillis())) {
				gameMap.put("isLocked", 1);
			
			}
			else {
				gameMap.put("isLocked", 0);
				
			}
			gameMap.put("imgPath", imgPath);
			gameMap.put("gameOpenCode", jsonStr);
			gameMap.put("isPlay", planGame.getIsPlay());
			gameMap.put("gameId", gameId);
			gameMap.put("gameClassId", game.getGameClassId());
			gameMap.put("gameName", gameName);
			gameMap.put("brief", game.getBrief());
			gameMap.put("benefit", game.getBenefit());
			gameMap.put("gameFileName", game.getGameFileName());
			gameMap.put("sequence", planGame.getSequence());
			games.add(gameMap);
			i++;
		}
		
		List<PlanGame> remainingPlans = PlanGame.dao.findRemaining(trainPlan.getTrainPlanId());
		switch (remainingPlans.size()) {
		case 0:
			setAttr("isTrainPlanEnd", 1);
			setAttr("planInfo", "非常棒！今日训练任务已完成！");
			break;
		case 1:
			setAttr("isTrainPlanEnd", 0);
			setAttr("planInfo", "加油加油，只剩一个任务了！");
			break;
		case 2:
			setAttr("isTrainPlanEnd", 0);
			setAttr("planInfo", "很轻松地完成了一个任务，继续努力！");
			break;
		case 3:
			setAttr("isTrainPlanEnd", 0);
			setAttr("planInfo", "很轻松地完成了一个任务，继续努力！");
			break;
		case 4:
			setAttr("isTrainPlanEnd", 0);
			setAttr("planInfo", "很轻松地完成了一个任务，继续努力！");
			break;
		case 5:
			setAttr("isTrainPlanEnd", 0);
			setAttr("planInfo", "今天有五个训练任务，快来热身吧！");
			break;
		default:
			setAttr("isTrainPlanEnd", 0);
			setAttr("planInfo", "训练计划生成出错，请联系管理员！");
			break;
		}
		setAttr("games", games);
		setAttr("isTestPlanEnd", 1);
		renderJson(new String[] {"isBegin","isTrainPlanEnd","isTestPlanEnd","planInfo","games"});
	}
	
	/**
	 * 获取训练计划中的下一个游戏
	 */
	@ActionKey("/api/game/testGame")
	public void testPlay() {
		
		Long userId = getParaToLong("userId");
		String userName = Account.dao.findById(userId).getUserName();
		List<TestPlan> userPlans = TestPlan.dao.remaining(userId);
		if (userPlans.size() > 0) {
			TestPlan testPlan = userPlans.get(0);
			Game game = Game.dao.findById(testPlan.getGameId());
			
			int isLike = 0;
			int gameId = game.getGameId();
			String gameName = game.getGameName();
			String jsonStr = "{'game':'"+game.getPathName()+"','datas':{'fit_test':'1','game_id':'"+gameId+"','game_param':'"+gameName+"','username':'"+userName+"','token':'"
						+ "uqiwejhbaskjdbasdioqw','game_user_setting':{'level':'1','time':'5','score':'0'},'updated_at':'"
						+DateTimeKit.getPlayDate()+"'}}";
			UserGame userGame = UserGame.dao.findByUserIdAndGameId(userId, gameId);
			if (userGame != null) {
				if (userGame.getLastPlayDate() != null && userGame.getLastPlayScore() != null) {
					jsonStr = "{'game':'"+game.getPathName()+"','datas':{'fit_test':'0','game_id':'"+gameId+"','game_param':'"+gameName+"','username':'"+userName+"','token':'"
							+ "uqiwejhbaskjdbasdioqw','game_user_setting':{'level':'"+
							(userGame.getLastLevel()==null ? 1 : userGame.getLastLevel())
							+"','time':'5','score':'"+userGame.getLastPlayScore()+"'},'updated_at':'"
							+userGame.getLastPlayDate()+"'}}";		
				}
				isLike = userGame.getIsLike();
			}
			String imgPath = "http://120.27.234.232/resources/images/games/" + game.getPadGameImg();
			
			Map<String, Object> turboGame = new HashMap<>();
			turboGame.put("sequence", testPlan.getSequence());
			turboGame.put("gameOpenCode", jsonStr);
			turboGame.put("isCollected", isLike);
			turboGame.put("imgPath", imgPath);
			turboGame.put("gameId", gameId);
			turboGame.put("gameClassId", game.getGameClassId());
			turboGame.put("gameName", gameName);
			turboGame.put("brief", game.getBrief());
			turboGame.put("benefit", game.getBenefit());
			turboGame.put("gameFileName", game.getGameFileName());
			
			renderJson("game",turboGame);
		} else {
			renderJson("msg", "适应性训练计划已完成！");
		}
		
	}
	
	/**
	 * 获取训练计划中的下一个游戏
	 */
	@ActionKey("/api/game/truboGame")
	public void turboPlay() {
		
		Long userId = getParaToLong("userId");
		String userName = Account.dao.findById(userId).getUserName();
		
		TrainPlan trainPlan = TrainPlan.dao.findByIdAndDate(userId, DateTimeKit.getDate());
		Integer trainPlanId = trainPlan.getTrainPlanId();
		List<PlanGame> userPlans = PlanGame.dao.findRemaining(trainPlanId);
		if (userPlans.size() > 0) {
			PlanGame planGame = userPlans.get(0);
			Game game = Game.dao.findById(planGame.getGameId());
			
			int isLike = 0;
			int gameId = game.getGameId();
			String gameName = game.getGameName();
			String jsonStr = "{'game':'"+game.getPathName()+"','datas':{'fit_test':'1','game_id':'"+gameId+"','game_param':'"+gameName+"','username':'"+userName+"','token':'"
						+ "uqiwejhbaskjdbasdioqw','game_user_setting':{'level':'1','time':'5','score':'0'},'updated_at':'"
						+DateTimeKit.getPlayDate()+"'}}";
			UserGame userGame = UserGame.dao.findByUserIdAndGameId(userId, gameId);
			if (userGame != null) {
				if (userGame.getLastPlayDate() != null && userGame.getLastPlayScore() != null) {
					jsonStr = "{'game':'"+game.getPathName()+"','datas':{'fit_test':'0','game_id':'"+gameId+"','game_param':'"+gameName+"','username':'"+userName+"','token':'"
						+ "uqiwejhbaskjdbasdioqw','game_user_setting':{'level':'"+
						(userGame.getLastLevel()==null ? 1 : userGame.getLastLevel())	
						+"','time':'5','score':'"+userGame.getLastPlayScore()+"'},'updated_at':'"
						+userGame.getLastPlayDate()+"'}}";	
				}
				isLike = userGame.getIsLike();
			}
			
			Map<String, Object> turboGame = new HashMap<>();
			
			List<GameHistory> histories = GameHistory.dao.findBestScore(userId, gameId);
			if (histories.size() > 0) {
				turboGame.put("bestScore", histories.get(0).getScore());
			} else {
				turboGame.put("bestScore", 0);
			}
			String imgPath = "http://120.27.234.232/resources/images/games/" + game.getPadGameImg();
			
			int isLocked = 0;
			if (planGame.getSequence() > 3) {
				long gameClassExpireTime = Order.dao.findExpireTimeByUserIdAndGameClassId(userId, game.getGameClassId());
				if (gameClassExpireTime <= System.currentTimeMillis()) {
					isLocked = 1;
				}
			}
			turboGame.put("isLocked", isLocked);
			turboGame.put("sequence", planGame.getSequence());
			turboGame.put("gameOpenCode", jsonStr);
			turboGame.put("isCollected", isLike);
			turboGame.put("imgPath", imgPath);
			turboGame.put("gameId", gameId);
			turboGame.put("gameClassId", game.getGameClassId());
			turboGame.put("gameName", gameName);
			turboGame.put("brief", game.getBrief());
			turboGame.put("benefit", game.getBenefit());
			turboGame.put("gameFileName", game.getGameFileName());
			
			
			renderJson("game",turboGame);
		} else {
			renderJson("msg","今日训练计划已完成，请明日再来~");
		}
		
	}
	
	/**
	 * 发送验证码，整合邮箱和手机
	 */
	@ActionKey("/api/sendCode")
	public void sendCode() {
		
		String type = getPara("type");
		String code = RandomStringUtils.random(4, "0123456789");
		boolean flag = true;
		if (Pattern.matches(emailReg, type)) {
			//邮箱
			String content = "【认知训练平台】你的验证码为:" + code + "&nbsp;&nbsp;&nbsp;&nbsp;5分钟内有效！";
			flag = EmailKit.send(type, "认知训练平台验证码", content);
			
		} else if (Pattern.matches(phoneReg, type)) {
			//手机
			flag = SmsKit.send(type, code);
		} else {
			renderJson("msg","你输入的邮箱或者手机号非法！请重新输入");
			return;
		}
		if (flag) {
			CacheKit.put("valiCode", type, code);//放入缓存，5分钟内有效
			setAttr("msg", "验证码发送成功，请注意查收！");
			setAttr("code", code);
			renderJson(new String[] {"msg","code"});
		} else { 
			renderJson("msg", "验证码发送失败，请稍后重试！");
		}
	}
	
	/**
	 * 验证验证码的有效性，整合邮箱和手机
	 */
	@ActionKey("/api/valiCode")
	public void valiCode() {
		
		String type = getPara("type");
		String code = getPara("code");
		if (code.equals(CacheKit.get("valiCode", type)))
			renderJson("msg", "vali");
		else 
			renderJson("msg", "not vali");
	}
	
	@ActionKey("/api/question")
	public void question() {
		
	}
	
	@ActionKey("/api/vipinfo")
	public void vipInfo() {
		Long userId = this.getParaToLong("userId");
		long expireTime = Order.dao.findExpireTimeByUserIdAndTrainClassId(userId, 1);
		// 默认非会员
		int isVip = 0;
		// 默认未解锁
		int isLocked = 0;
		List<Integer> gameIds = new ArrayList<>();
		int day = -1;
		if (expireTime > System.currentTimeMillis()) {
			// 已解锁,会员未到期
			isLocked = 1;
			// 会员
			isVip = 1;
		    long t = expireTime - System.currentTimeMillis();
		    day = (int) (t / 3600 / 1000 / 24);
		    
		}
		setAttr("isVip", isVip);
		setAttr("day", day);
		setAttr("msg", "success");
		renderJson(new String[] {"msg", "isVip", "day"});
	}
	
	/**
     * 第三方登录
     * @param openId
     * @param platform 平台 1：QQ 2：微信
     * @return
     */
    @ActionKey("/api/oauth/login")
    public void oauthLogin() {
    	String openId = this.getPara("openId");
    	String nickname = this.getPara("nickname");
    	String cover = this.getPara("cover");
    	Integer platform = this.getParaToInt("platform");
    	Integer gender = this.getParaToInt("gender");
    	
        OauthUser oauthUser = new OauthUser();
        oauthUser.setOpenId(openId);
        oauthUser.setCover(cover);
        oauthUser.setNickname(nickname);
        oauthUser.setPlatform(platform);
        oauthUser.setGender(gender);
        try {
            boolean flag = OauthUser.dao.oauthLogin(oauthUser);
            if (flag) {
//                Map stuUser;
                // 查询stuuser表
                Account account = Account.dao.findById(oauthUser.getUserId());
//                stuUser = userService.findOne(oauthUser.getUserId());
//                stuUser.put("bindtype", 0);
                
                setAttr("msg", "success");
                Map<String,Object> returnUser = OauthUser.dao.getReturnInfo(account, account.getUserName(), account.getBirthday(), account.getPassword());
                returnUser.put("bindtype", 0);
                setAttr("account", returnUser);
                
                QuestionStatus status = QuestionStatus.dao.findById(account.getUserId());
    			setAttr("questionStatus1", status.getQuestion1Status());
    			setAttr("questionStatus2", status.getQuestion2Status());
    			renderJson(new String[] {"msg", "account","questionStatus1","questionStatus2"});
//                return ApiResult.success(account, "userInfo");
            }
            else {
//                Map<String, Object> paramsMap = new HashMap<>();
//                paramsMap.put("bindtype", 1);
            	setAttr("account",  new Record().set("bindtype", 1));
            	setAttr("msg", "success");
                renderJson(new String[] {"msg", "account"});
//                return ApiResult.success(paramsMap, "userInfo");
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderJson("msg", "系统错误");
        }
    }

    /**
     * 验证手机号
     * @param mobile 手机号
     * @return result
     */
    @ActionKey(value = "/api/checkMobile")
    public void checkMobile() {
    	String mobile = this.getPara("mobile");
        try {
//            Map user;
//            if (ReKit.isMatch(ReKit.regExp_email, mobile)) {
//                user = userService.findByEmail(mobile);
//            }
//            else if (ReKit.isMatch(ReKit.regExp_mobile, mobile)){
//                user = userService.findByMobile(mobile);
//            }
//            else {
//                user = userService.findByNickname(mobile);
//            }
        	Account account = Account.dao.isAccountExist(mobile);
            boolean isExit = (account!= null && account.get("userId") != null);
            setAttr("isExistisExist", (isExit ? "1" : "0"));
            setAttr("msg", "success");
            renderJson(new String[] {"msg", "isExistisExist"});
//            return ApiResult.success((isExit ? "0" : "1"),"isExist");
        } catch (Exception e) {
            e.printStackTrace();
//            return ApiResult.failure();
            renderJson("msg", "系统错误");
        }
    }

    /**
     * 绑定手机号
     * @param openId 第三方登录号
     * @return result
     */
    @ActionKey("/api/bindmobile")
    public void bindMobile() {
    	String openId = this.getPara("openId");
    	String mobile = this.getPara("mobile");
    	String userName = this.getPara("userName");
    	String code = this.getPara("code");
    	String password = this.getPara("password");
    	
//		if (!code.equals(CacheKit.get("valiCode", mobile))) {
//			renderJson("msg", "vali");
//			return;
//		}
        try {
        	Date birth = this.getParaToDate("date");
        	setAttr("msg", "success");
        	Map<String, Object> oauth = OauthUser.dao.bindMobile(openId, userName, birth, mobile, code, password);
        	
        	setAttr("account", oauth);
//            return oauthUserService.bindMobile(openId, mobile, code, password);
        	QuestionStatus status = QuestionStatus.dao.findById(oauth.get("userId"));
  			setAttr("questionStatus1", status.getQuestion1Status());
  			setAttr("questionStatus2", status.getQuestion2Status());
  			renderJson(new String[] {"msg", "account","questionStatus1","questionStatus2"});
        } catch (Exception e) {
            e.printStackTrace();
//            return ApiResult.failure();
            renderJson("msg", "系统错误");
        }
    }
    
    
    // 绑定微信
    @ActionKey("/api/bindTencent")
    public void bindTencent() {
    	Long userId = this.getParaToLong("userId");
    	String openId = this.getPara("openId");
    	String nickname = this.getPara("nickname");
    	Integer gender = this.getParaToInt("gender");
    	Integer platform = this.getParaToInt("platform");
    	String cover = this.getPara("cover");
    	
    	try {
			OauthUser oauthUser2 = OauthUser.dao.findByOpenId(openId);
			if (oauthUser2 != null ) {
				if (oauthUser2.getUserId() != null) {
					renderJson("msg", "微信号不能重复绑定");
				}
				else {
					oauthUser2.setUserId(userId);
					oauthUser2.update();
					setAttr("msg", "success");
			    	renderJson(new String[]{"msg"});
				}
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	OauthUser ouathUser = new OauthUser();
    	ouathUser.setUserId(userId);
    	ouathUser.setOpenId(openId);
    	ouathUser.setNickname(nickname);
    	ouathUser.setCover(cover);
    	ouathUser.setGender(gender);
    	ouathUser.setPlatform(platform);
    	ouathUser.setCreateTime(System.currentTimeMillis());
    	ouathUser.save();
    	
    	Account account = Account.dao.findById(userId);
    	if (platform == 1) {
    		// QQ
    		account.setQqId(openId);
    	}
    	else {
    		// weixin
    		account.setWeixinId(openId);
    	}
    	
    	account.update();
    	setAttr("msg", "success");
    	renderJson(new String[]{"msg"});
    }
    
    //解绑微信
    @ActionKey("/api/unbindTencent")
    public void unbindTencent() {
    	String openId = this.getPara("openId");
    	Long userId = this.getParaToLong("userId");
    	Integer platform = this.getParaToInt("platform");
    	if (platform == 1) {
    		// QQ
    		OauthUser.dao.unbindQQ(openId, userId);
    	}
    	else {
    		// weixin
    		OauthUser.dao.unbind(openId, userId);
    	}
    	setAttr("msg", "success");
    	renderJson(new String[]{"msg"});
    }
}




