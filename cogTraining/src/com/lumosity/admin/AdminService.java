package com.lumosity.admin;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.lumosity.model.*;
import com.lumosity.utils.OrderKit;
import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class AdminService {

    public static  AdminService me=new AdminService();

    /**
     * 根据凭证号寻找临时凭证
     * @param vouch
     * @return
     */
    public TrialAdminTemp findByVouch(String vouch){
        try {
            TrialAdminTemp trialAdminTemp=TrialAdminTemp.dao.findFirst("select * from trial_admin_temp where vouch=? and status=0",vouch);
            if(trialAdminTemp!=null){

                //检查是否失效
                Date Expires=trialAdminTemp.getExpires();
                Date Now=new Date();
                //放宽条件
                if(Expires.getTime()-Now.getTime()<0){
                    //过期
                    return null;
                }else{
                    return trialAdminTemp;
                }

            }else{
                return  null;
            }

        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    public TrialAdmin findTAByOpenid(String openid){
        try{
            TrialAdmin trialAdmin=TrialAdmin.dao.findFirst("select * from trial_admin where wx_openid=? and invalidate=0",openid);
            return trialAdmin;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过二维码json串查找用户
     * @param str
     * @return
     */
    public Account findAccByQRStr(String str){
        try{
            JSONObject obj=new JSONObject(str);
            if(obj.has("cogdaily")){
                //暂时只获取id 有问题再改
                int userId=obj.getInt("id");
                Account account=Account.dao.findById(userId);

                return  account;

            }else{
                return  null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }

    }


    public TrialRecord findByUserId(Long userId){
        try{
            TrialRecord trialRecord =TrialRecord.dao.findFirst("select * from trial_record where userId=?",userId);
            return  trialRecord;
        }
        catch (Exception e){
            e.printStackTrace();
            return  null;
        }

    }

    public boolean createTrialRecordAndOrder(Account acc,TrialAdmin ta){
        try{

            //开启事务
            boolean transaction= Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    //内部代码不要catch Exception

                    //设置Ta
                    ta.setAllocateNum(ta.getAllocateNum()+1);
                    if(!ta.update()){
                        //更新失败滚回
                        ta.setAllocateNum(ta.getAllocateNum()-1);
                        return  false;
                    }

                    //设置TR
                    //创建分配记录
                    TrialRecord trialRecord=new TrialRecord();
                    trialRecord.setTaAdminId(ta.getId());
                    trialRecord.setUserId(acc.getUserId());
                    trialRecord.setTime(new Date());
                    trialRecord.setDuration(AdminConstant.TRIAL_DURATION);
                    if(!trialRecord.save()){
                        return  false;
                    }
                    //创建订单
                    String trainClassOrderNo= OrderKit.generateSimpleOrderNo();
                    //获取游戏类型列表
                    List<GameClass> gameList=GameClass.dao.findByTrainClass(1);
                    long userId=acc.getUserId();

                    for (GameClass gameClass: gameList){

                        String OrderNo=OrderKit.generateOrderNo();
                        boolean result=Order.dao.saveAllotOrder(OrderNo,userId,gameClass.getGameClassId(),AdminConstant.TRIAL_DURATION,trainClassOrderNo);
                        if(!result){
                            return  false;
                        }


                    }
                    //修改用户过期时间
                    //每小类有独立过期时间 ，不修改用户过期时间 是否会出错？


                    return true;
                }
            });

            return transaction;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }

    }



}
