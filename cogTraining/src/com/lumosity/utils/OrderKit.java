package com.lumosity.utils;

import java.util.UUID;

public class OrderKit {

	public static String generateOrderNo() {
		String dataStr = DateTimeKit.getStringDate().replace("-",  "");
		String uuid = UUID.randomUUID().toString().replace("-", "");
		return dataStr + uuid;
	}
	
	public static String generateSimpleOrderNo() {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		return uuid;
	}
	
	public static void main(String[] args) {
		System.out.println(generateOrderNo());
	}
}
