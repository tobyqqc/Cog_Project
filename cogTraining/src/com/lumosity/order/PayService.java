package com.lumosity.order;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 2018-8-22 create by fupan
 * 支付时长与金额获取逻辑
 */
public class PayService {

    /**
     * 本类中抛出的关于支付数据的异常
     * 终止支付操作
     */
    public class PayServiceException extends Exception{
        PayServiceException(String msg){
            super(msg);
        }
    }
    public static PayService me=new PayService();
    public static int[] duration;
    public static float[] cost;
    private JSONArray scheme;
    PayService(){
        duration=new int[]{1,3,12};
        cost=new float[]{25.0f,68.0f,248.0f};

        scheme=new JSONArray();
        for(int i=0;i<duration.length;i++){
            JSONObject item=new JSONObject();
            item.put("duration",duration[i]);
            item.put("unit",String.format("%.2f",cost[i]/duration[i]));
            item.put("cost",cost[i]);
            scheme.put(item);
        }

    }


    /**
     * 根据订单时长获取所需支付的金额
     * 可能没有对应时长
     * @param dur
     * @return
     */
    public float getCostByDuration(int dur) throws PayServiceException{
        for (int i=0;i<duration.length;i++){
            if(duration[i]==dur){
                return cost[i];
            }
        }
        throw new PayServiceException("订单时长错误！");
    }

    /**
     * 获取支付方案
     * @return
     */
    public JSONArray getScheme(){
        return  scheme;
    }
}
