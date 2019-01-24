package com.lumosity.model;

import java.util.Date;

import com.lumosity.model.base.BaseIosReceipt;

public class IosReceipt extends BaseIosReceipt<IosReceipt>{
	private static final long serialVersionUID = -2205768905069865918L;
	public static IosReceipt dao = new IosReceipt();
	
	public IosReceipt findByReceipt(String receipt) {
		return findFirst("select * from ios_receipt where receipt = ?", receipt);
	}
	
	public boolean isExist(String receipt) {
		return !(findByReceipt(receipt) == null);
	}
	
	public void save(String receipt) {
		IosReceipt iosReceipt = new IosReceipt();
		iosReceipt.setReceipt(receipt);
		iosReceipt.setCreateDate(new Date());
		iosReceipt.save();
	}
}
 