package com.lumosity.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.lumosity.model.base.BaseOauthUser;
import com.lumosity.utils.DateTimeKit;

/**
 * 第三方用户
 * Created by yesong on 2017/7/17 0017.
 */
@SuppressWarnings("serial")
public class OauthUser extends BaseOauthUser<OauthUser> {

	public static OauthUser dao = new OauthUser();
	
    public Boolean oauthLogin(OauthUser _oauthUser) throws Exception {
    	  OauthUser oauthUser = findByOpenId(_oauthUser.getOpenId());
          if (oauthUser == null) {
              // 新增第三方用户信息
              oauthUser = new OauthUser();
              oauthUser.setNickname(_oauthUser.getNickname());
              oauthUser.setCover(_oauthUser.getCover());
              oauthUser.setCreateTime(System.currentTimeMillis());
              oauthUser.setOpenId(_oauthUser.getOpenId());
              oauthUser.setPlatform(_oauthUser.getPlatform());
              create(oauthUser);
              //跳转至手机绑定页面
              return false;
          }
          else {

              _oauthUser.setUserId(oauthUser.getUserId());
              return oauthUser.getUserId() != null;
          }
    }

    public OauthUser findByOpenId(String openId) throws Exception {
        try{
            return findFirst("SELECT o.`id`, o.`user_id`, o.`cover`, o.`create_time`, o.`user_id`, o.`nickname`, o.`open_id`, o.`platform` FROM tb_oauth o WHERE o.`open_id` = ?", openId);

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
       }

    public void create(OauthUser oauthUser) throws Exception {
    	oauthUser.save();
    }

    public Map<String, Object> bindMobile(String openId, String userName, Date birth, String mobile, String code, String password) throws Exception {
    	 OauthUser oauthUser = findByOpenId(openId);
         Account account = Account.dao.isAccountExist(mobile);
         String emailReg = "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
     	 String phoneReg = "^1[3|4|5|7|8][0-9]{9}$";
     	 Account loginInfo = null;
     	 String authCol = oauthUser.getPlatform() == 1 ? "qqId" : "weixinId";
     	 System.out.println("authCol:" + authCol);
     	 System.out.println("bindMobile.openId:" + oauthUser.getOpenId());
         if (account == null) {
             // 新增用户信息
//             stuUser = new StuUser(mobile, MD5.md5(password), oauthUser.getNickname(), oauthUser.getCover());
//             Result result = registerService.register(stuUser, code, true);
        	
 			if (Pattern.matches(emailReg, mobile)) {
 				//邮箱
 				new Account().set("userName", userName).set("email", mobile).set("createDate", DateTimeKit.getDate())
 					.set("password", password).set("birthday", birth).set(authCol, oauthUser.getOpenId()).save();
 				loginInfo = Account.dao.findByEmailAndPwd(mobile, password);
 			} else if (Pattern.matches(phoneReg, mobile)) {
 				//手机
 				new Account().set("userName", userName).set("mobileId", mobile).set("createDate", DateTimeKit.getDate())
 					.set("password", password).set("birthday", birth).set(authCol, oauthUser.getOpenId()).save();
 				loginInfo = Account.dao.findByPhoneAndPwd(mobile, password);
 			} else {
 				//非法输入
// 				renderJson("msg", "你输入的邮箱或者手机号非法！请重新输入");
 				throw new Exception("你输入的邮箱或者手机号非法！请重新输入");
 			}
 			
 			Map<String, Object> returnInfo = getReturnInfo(loginInfo, userName, birth, password);
 			Long userId = loginInfo.getUserId();
// 			setAttr("account", returnInfo);
 			
 			//创建适应性训练计划(固定为三种类型游戏)
 			new TestPlan().set("userId", userId).set("gameId", 14).set("sequence", 1).set("isPlay", 0).save();//speed_match
 			new TestPlan().set("userId", userId).set("gameId", 7).set("sequence", 2).set("isPlay", 0).save();//lost_in_migration
 			new TestPlan().set("userId", userId).set("gameId", 13).set("sequence", 3).set("isPlay", 0).save();//memory_matrix
 			//创建用户问卷状态
 			new QuestionStatus().set("userId", userId).set("question1Status", 0).set("question2Status", 0).set("survey", "").save();
             
 			oauthUser.setUserId(userId);
 			
 			bindUserId(oauthUser);
            return returnInfo;
         }
         else {
             // 第三方账号绑定已存在用户
             oauthUser.setUserId(account.getUserId());
             bindUserId(oauthUser);
             account.set(authCol, oauthUser.getOpenId());
             account.update();
             return getReturnInfo(account, userName, birth, password);
         }
    }

    public void bindUserId(OauthUser oauthUser) throws Exception {
    	oauthUser.update();
    }
    
    public Map<String, Object> getReturnInfo(Account account,String userName, Date birth, String password) {
    		Long userId = account.getUserId();
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
			returnInfo.put("qqId", qqId);
			returnInfo.put("isMember", 0);
			return returnInfo;
    }
    
    /**
     * 解除绑定
     * @param openId
     * @param userId
     */
    public void unbind(String openId, Long userId) {
    	try {
			OauthUser oauthUser = findByOpenId(openId);
			if (oauthUser != null && userId.longValue() == oauthUser.getUserId().longValue()) {
				deleteByOpenId(oauthUser);
				Account account = Account.dao.findById(userId);
				account.setWeixinId("");
				account.update();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
    /**
     * 解除绑定
     * @param openId
     * @param userId
     */
    public void unbindQQ(String openId, Long userId) {
    	try {
			OauthUser oauthUser = findByOpenId(openId);
			if (oauthUser != null && userId.longValue() == oauthUser.getUserId().longValue()) {
				deleteByOpenId(oauthUser);
				Account account = Account.dao.findById(userId);
				account.setQqId("");;
				account.update();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 根据openid删除绑定信息
     * @param oauthUser
     */
    public void deleteByOpenId(OauthUser oauthUser) {
    	oauthUser.delete();
    }
}
