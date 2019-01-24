package com.lumosity.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.alibaba.fastjson.JSONArray;

public class TestJson {

	 static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	
	public static void main(String[] args) {
        String aa = "[{\"quantity\":\"1\", \"product_id\":\"cogdailyipadl001\", \"transaction_id\":\"1000000372990628\", \"original_transaction_id\":\"1000000372990628\", \"purchase_date\":\"2018-02-04 03:17:19 Etc/GMT\", \"purchase_date_ms\":\"1517714239000\", \"purchase_date_pst\":\"2018-02-03 19:17:19 America/Los_Angeles\", \"original_purchase_date\":\"2018-02-04 03:17:19 Etc/GMT\", \"original_purchase_date_ms\":\"1517714239000\", \"original_purchase_date_pst\":\"2018-02-03 19:17:19 America/Los_Angeles\", \"is_trial_period\":\"false\"}, {\"quantity\":\"1\", \"product_id\":\"cogdailyipadl001\", \"transaction_id\":\"1000000372998022\", \"original_transaction_id\":\"1000000372998022\", \"purchase_date\":\"2018-02-04 06:31:56 Etc/GMT\", \"purchase_date_ms\":\"1517725916000\", \"purchase_date_pst\":\"2018-02-03 22:31:56 America/Los_Angeles\", \"original_purchase_date\":\"2018-02-04 06:31:56 Etc/GMT\", \"original_purchase_date_ms\":\"1517725916000\", \"original_purchase_date_pst\":\"2018-02-03 22:31:56 America/Los_Angeles\", \"is_trial_period\":\"false\"}, {\"quantity\":\"1\", \"product_id\":\"cogdailyipadl001\", \"transaction_id\":\"1000000373000041\", \"original_transaction_id\":\"1000000373000041\", \"purchase_date\":\"2018-02-04 07:30:30 Etc/GMT\", \"purchase_date_ms\":\"1517729430000\", \"purchase_date_pst\":\"2018-02-03 23:30:30 America/Los_Angeles\", \"original_purchase_date\":\"2018-02-04 07:30:30 Etc/GMT\", \"original_purchase_date_ms\":\"1517729430000\", \"original_purchase_date_pst\":\"2018-02-03 23:30:30 America/Los_Angeles\", \"is_trial_period\":\"false\"}, {\"quantity\":\"1\", \"product_id\":\"cogdailyipadl001\", \"transaction_id\":\"1000000373000565\", \"original_transaction_id\":\"1000000373000565\", \"purchase_date\":\"2018-02-04 07:49:26 Etc/GMT\", \"purchase_date_ms\":\"1517730566000\", \"purchase_date_pst\":\"2018-02-03 23:49:26 America/Los_Angeles\", \"original_purchase_date\":\"2018-02-04 07:49:26 Etc/GMT\", \"original_purchase_date_ms\":\"1517730566000\", \"original_purchase_date_pst\":\"2018-02-03 23:49:26 America/Los_Angeles\", \"is_trial_period\":\"false\"}, {\"quantity\":\"1\", \"product_id\":\"cogdailyipadl001\", \"transaction_id\":\"1000000373003725\", \"original_transaction_id\":\"1000000373003725\", \"purchase_date\":\"2018-02-04 09:54:49 Etc/GMT\", \"purchase_date_ms\":\"1517738089000\", \"purchase_date_pst\":\"2018-02-04 01:54:49 America/Los_Angeles\", \"original_purchase_date\":\"2018-02-04 09:54:49 Etc/GMT\", \"original_purchase_date_ms\":\"1517738089000\", \"original_purchase_date_pst\":\"2018-02-04 01:54:49 America/Los_Angeles\", \"is_trial_period\":\"false\"}, {\"quantity\":\"1\", \"product_id\":\"cogdailyipadl002\", \"transaction_id\":\"1000000372990211\", \"original_transaction_id\":\"1000000372990211\", \"purchase_date\":\"2018-02-04 03:03:06 Etc/GMT\", \"purchase_date_ms\":\"1517713386000\", \"purchase_date_pst\":\"2018-02-03 19:03:06 America/Los_Angeles\", \"original_purchase_date\":\"2018-02-04 03:03:06 Etc/GMT\", \"original_purchase_date_ms\":\"1517713386000\", \"original_purchase_date_pst\":\"2018-02-03 19:03:06 America/Los_Angeles\", \"is_trial_period\":\"false\"}, {\"quantity\":\"1\", \"product_id\":\"cogdailyipadl002\", \"transaction_id\":\"1000000372990627\", \"original_transaction_id\":\"1000000372990627\", \"purchase_date\":\"2018-02-04 03:16:57 Etc/GMT\", \"purchase_date_ms\":\"1517714217000\", \"purchase_date_pst\":\"2018-02-03 19:16:57 America/Los_Angeles\", \"original_purchase_date\":\"2018-02-04 03:16:57 Etc/GMT\", \"original_purchase_date_ms\":\"1517714217000\", \"original_purchase_date_pst\":\"2018-02-03 19:16:57 America/Los_Angeles\", \"is_trial_period\":\"false\"}, {\"quantity\":\"1\", \"product_id\":\"cogdailyipadl002\", \"transaction_id\":\"1000000373007062\", \"original_transaction_id\":\"1000000373007062\", \"purchase_date\":\"2018-02-04 11:55:46 Etc/GMT\", \"purchase_date_ms\":\"1517745346000\", \"purchase_date_pst\":\"2018-02-04 03:55:46 America/Los_Angeles\", \"original_purchase_date\":\"2018-02-04 11:55:46 Etc/GMT\", \"original_purchase_date_ms\":\"1517745346000\", \"original_purchase_date_pst\":\"2018-02-04 03:55:46 America/Los_Angeles\", \"is_trial_period\":\"false\"}, {\"quantity\":\"1\", \"product_id\":\"cogdailyipadl003\", \"transaction_id\":\"1000000373007007\", \"original_transaction_id\":\"1000000373007007\", \"purchase_date\":\"2018-02-04 11:54:44 Etc/GMT\", \"purchase_date_ms\":\"1517745284000\", \"purchase_date_pst\":\"2018-02-04 03:54:44 America/Los_Angeles\", \"original_purchase_date\":\"2018-02-04 11:54:44 Etc/GMT\", \"original_purchase_date_ms\":\"1517745284000\", \"original_purchase_date_pst\":\"2018-02-04 03:54:44 America/Los_Angeles\", \"is_trial_period\":\"false\"}]";
        
		List<ApplePayReturnClass> result = JSONArray.parseArray(aa, ApplePayReturnClass.class);
		int i = 0;
		long lastTime = 0L;
		try {
			for (int m = 0; m < result.size(); m++) {
				ApplePayReturnClass demoJson2 = result.get(m);
				System.out.println("demo:" + demoJson2.getTransaction_id());
				String lastTimeStr = demoJson2.getPurchase_date();
				long time = formatter.parse(lastTimeStr).getTime();
				if (lastTime == 0L) {
					lastTime = time;
				}
				else {
					if (lastTime < time) {
						lastTime = time;
						i = m;
					}
				}
			}
			ApplePayReturnClass lastDemoJson = result.get(i);
			System.out.println("lastDemo:" + lastDemoJson.getTransaction_id());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
