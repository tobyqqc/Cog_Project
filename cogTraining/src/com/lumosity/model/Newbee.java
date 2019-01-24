package com.lumosity.model;

import com.lumosity.model.base.BaseNewbee;

@SuppressWarnings("serial")
public class Newbee extends BaseNewbee<Newbee>{

	public static Newbee dao = new Newbee();
	
	public Newbee findByGameId(Integer gameId) {
		return findFirst("select * from newbee where gameId = ?", gameId);
	}
	
	public void update(Newbee newBee) {
		newBee.update();
	}
}
