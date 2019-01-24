package com.lumosity.admin;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.lumosity.login.oauth.WxService;
import com.lumosity.login.oauth.meta.ShareLoginDict;
import com.lumosity.model.Account;
import com.lumosity.model.TrialAdmin;
import com.lumosity.model.TrialAdminTemp;
import com.lumosity.model.TrialRecord;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Clear
@Before(AdminInterceptor.class)
public class AdminController extends Controller {


    /**
     * 分配试用套餐扫码页
     */
    public void scanQrForTrial(){

            String appid= ShareLoginDict.weixinMpAppid;
            //需要拼接参数
            String param=getRequest().getQueryString();
            String url=getRequest().getRequestURL().toString();
            if(param!=null&&param.length()!=0){
                url+="?"+param;
            }

            String timestamp=String.valueOf(new Date().getTime()/1000);
            String nonceStr=UUID.randomUUID().toString().substring(0,5);
            String signature=WxAppService.me.getJSAPISignature(nonceStr,timestamp,url);
            if(signature==null){
                renderText("服务器内部错误请稍后再试或者联系管理员！");
            }else{
                setAttr("appId",appid);
                setAttr("timestamp",timestamp);
                setAttr("nonceStr",nonceStr);
                setAttr("signature",signature);

                render("scanQrForTrial.html");
            }
    }

    //扫码数据提交页 获取二维码用户信息 并开始使oauth 获取openid
    public void scanSubmit(){
        String user=getPara("user");
        if(user==null){
            setAttr("resultStr","参数错误！");
            render("result.html");
        }else {
            setSessionAttr("user",user);
            //分发微信跳转地址
            String random=UUID.randomUUID().toString().substring(0,3);
            setSessionAttr("state",random);
            String url=ShareLoginDict.DOMAIN_URL+"/admin/scanCallBack";
            String addr= WxService.oauth2buildAuthorizationUrl(url,
                    ShareLoginDict.weixinMpScope,random);

            redirect(addr);
        }
    }

    /**
     * 扫码获取openid回调页
     */
    public void scanCallBack(){

        String state=getSessionAttr("state");
        removeSessionAttr("state");
        String State=getPara("state");
        if(!State.equalsIgnoreCase(state)){
            setAttr("resultStr","请从微信端正常访问页面！");
            render("result.html");
            return;
        }

        try{
            //取出user 并清理session
            String user=getSessionAttr("user");
            if(user!=null){
                removeSessionAttr("user");

                //取得openid
                String code=getPara("code");
                WxMpOAuth2AccessToken wxMpOAuth2AccessToken = WxService.oauth2getMpAccessToken(code);
                WxMpUser wxMapUser = WxService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);

                //判断openid 权限
                TrialAdmin ta=AdminService.me.findTAByOpenid(wxMapUser.getOpenId());
                //没有操作权限
                if(ta==null){
                    setAttr("resultStr","您没有操作权限！");
                    render("result.html");
                    return;
                }

                //解析user数据
                Account acc=AdminService.me.findAccByQRStr(user);
                //根据json串没有找到对应用户
                if(acc==null){
                    setAttr("resultStr","二维码识别错误，请确保扫描正确的用户二维码！");
                    render("result.html");
                    return;
                }
                //是否分配过试用套餐
                TrialRecord trialRecord=AdminService.me.findByUserId(acc.getUserId());
                if(trialRecord!=null){
                    //分配过试用套餐
                    setAttr("resultStr","该用户已经分配过试用套餐！");
                    render("result.html");
                    return;
                }

                //开始创建分配记录与分配订单
                boolean result=AdminService.me.createTrialRecordAndOrder(acc,ta);
                if(result){
                    setAttr("resultStr","分配成功！");
                }else{
                    setAttr("resultStr","发生错误，请重试或联系管理员！");
                }

                render("result.html");

            }else{
                //错误
                setAttr("resultStr","错误");
                render("result.html");
            }

        }catch (Exception e){
            e.printStackTrace();
            setAttr("resultStr","异常");
            render("result.html");
        }

    }

    /**
     * 获取操作者 openid 并 操作数据库
     */
    public void addAdmin(){
        String vouch=getPara("vouch");
        if(vouch==null){
            setAttr("resultStr","参数错误！");
            render("result.html");
        }else{
            setSessionAttr("vouch",vouch);

            String random=UUID.randomUUID().toString().substring(0,3);
            setSessionAttr("state",random);
            String url=ShareLoginDict.DOMAIN_URL+"/admin/callBack";
            String addr= WxService.oauth2buildAuthorizationUrl(url,
                    ShareLoginDict.weixinMpScope,random);

            redirect(addr);
        }
    }

    /**
     * 取操作者openid 回调页
     */
    public void callBack(){
        try{
            String state=getSessionAttr("state");
            removeSessionAttr("state");
            String State=getPara("state");
            if(!State.equalsIgnoreCase(state)){
                setAttr("resultStr","请从微信端正常访问页面！");
                render("result.html");
                return;
            }
            String vouch=getSessionAttr("vouch");
            String code=getPara("code");
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = WxService.oauth2getMpAccessToken(code);
            WxMpUser wxMapUser = WxService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            String openid=wxMapUser.getOpenId();
            String nickName=wxMapUser.getNickname();
            TrialAdminTemp trialAdminTemp= AdminService.me.findByVouch(vouch);
            if(trialAdminTemp!=null){
                //修改凭证相关内容
                trialAdminTemp.setWxOpenid(openid);
                trialAdminTemp.setWxNickname(nickName);
                trialAdminTemp.setStatus(1);
                if(trialAdminTemp.update()){
                    removeSessionAttr("vouch");
                    setAttr("resultStr","验证成功");
                    render("result.html");
                }else {
                    setAttr("resultStr","更新失败，请尝试重试或者联系管理员！");
                    render("result.html");
                }

            }else{
                setAttr("resultStr","验证失败，二维码可能已经过期，请尝试刷新二维码！");
                render("result.html");
            }


        }catch (Exception e){
            e.printStackTrace();
            setAttr("resultStr","发生错误，请尝试重试或者联系管理员！");
            render("result.html");

        }

    }
}
