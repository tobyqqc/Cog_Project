package com.lumosity.model;

import com.lumosity.model.base.BaseUserGame;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class UserGame extends BaseUserGame<UserGame> {
	public static final UserGame dao = new UserGame();
	
	public UserGame findByUserIdAndGameId(Long userId, Integer gameId) {
		return findFirst("select userGameId,userId,gameId,isLocked,isLike,lastPlayDate,lastPlayScore,playTimes,lastLevel from user_game where userId = ? and gameId = ?", userId, gameId);
		
	}
}
