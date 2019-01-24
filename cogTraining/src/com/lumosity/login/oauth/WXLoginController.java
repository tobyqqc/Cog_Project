package com.lumosity.login.oauth;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

import com.lumosity.login.oauth.meta.ShareLoginDict;
import com.lumosity.model.Account;
import com.lumosity.model.OauthUser;

import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.json.JSONObject;

import java.sql.SQLException;

/**
 * 微信绑定登录 控制器
 * 重写微信登录绑定逻辑
 * /wxlogin
 * fupan 2018-8-8
 */
public class WXLoginController  extends Controller {



    /**
     * 微信带code回调页
     *
     */
    public void index(){
        String code=getPara("code");
        try{
            //换取accessToken等用户信息
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = WxService.oauth2getMpAccessToken(code);
            WxMpUser wxMapUser = WxService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            setSessionAttr("wxMpUser",wxMapUser);

            String openId = wxMapUser.getOpenId();
            String nickname = wxMapUser.getNickname();
            String cover = wxMapUser.getHeadImgUrl();
            Integer platform = 2;
            Integer gender = (wxMapUser.getSex().equals("男") ? 0 : 1);



            OauthUser oauthUser = new OauthUser();
            oauthUser.setOpenId(openId);
            oauthUser.setCover(cover);
            oauthUser.setNickname(nickname);
            oauthUser.setPlatform(platform);
            oauthUser.setGender(gender);

            //原代码逻辑
            //找到 oauth 但是没有 userid的情况 算找不到 用户
            System.out.println(oauthUser.getOpenId());

            boolean flag=OauthUser.dao.oauthLogin(oauthUser);
            if(flag){
                //已经绑定 登录并跳转
                //设置 WXUser session
                setSessionAttr("WXUser",getSessionAttr("wxMpUser"));

                Account account=Account.dao.findById(oauthUser.getUserId());
                setSessionAttr("userInfo", account);
                String to=getPara("to");
                if(to!=null){
                    redirect(to);
                }else{
                    redirect("/home");
                }
            }else{
                //未绑定 渲染绑定页
                //如果包含to 参数 则提示是否绑定
                //并设置绑定成功后 跳转页面
                boolean hasNotify=false;        //页面是否提示绑定
                String to=getPara("to");
                if(to!=null){
                    hasNotify=true;
                    setAttr("to",to);
                }
                setAttr("hasNotify",hasNotify);


                //当前是否有登录
                Account account=getSessionAttr("userInfo");
                if(account!=null){
                    //已经有登录
                    String username=account.getUserName();
                    String phone=account.getMobileId();
                    String email=account.getEmail();
                    String acc=phone;

                    if(phone==null||phone.length()==0){
                        acc=email;
                    }


                    setAttr("hasLogin",true);
                    setAttr("display","none");
                    setAttr("username",account.getUserName());
                    setAttr("account",acc);

                }else{
                    setAttr("hasLogin",false);
                    setAttr("display","block");


                }
                render("/bind_weixin.html");

            }


        }catch (Exception e){
            e.printStackTrace();
            renderText("异常，请重微信端正常访问或联系管理员！");
        }

    }

    /**
     * 微信授权页面
     */
    public void oauth(){
        //根据是否是微信端开始分发链接
        String type=getPara("type");
        String to=getPara("to");
        if("weixin".equalsIgnoreCase(type)){
            //判断是否微信登录
            WxMpUser WXUser=getSessionAttr("WXUser");
            if(WXUser==null){
                //没有最终的微信账号信息
                //开始拼接 授权地址

                //微信登录回调地址 /wxlogin
                String redirect_url= ShareLoginDict.WXLoginNotify;
                if(to!=null){
                    redirect_url+="?to="+to;
                }
                String addr=WxService.oauth2buildAuthorizationUrl(redirect_url,
                        ShareLoginDict.weixinMpScope,ShareLoginDict.weixinState);

                redirect(addr);
            }
            else{
                //微信已经登录 开始分发跳转
                if(to!=null){
                    redirect(to);
                }else{
                    redirect("/");
                }

            }

        }else{

            //走原有逻辑 没有修改
            redirect(WxService.oauth2buildQrconnectUrl(ShareLoginDict.weixinNotify,
                    ShareLoginDict.weiScope, ShareLoginDict.weixinState));

        }


    }

    /**
     * 微信绑定功能 操作数据库 开启事务
     */

    public void bind(){

        //检查是否存在 wxMapUser
        WxMpUser wxMpUser =getSessionAttr("wxMpUser");
        if(wxMpUser==null){
            //非法访问 跳转oauth 授权页
            //redirect("/wxlogin/oauth");
            JSONObject obj=new JSONObject();
            obj.put("code",0);
            obj.put("msg","请从微信端正常访问！");
            renderJson(obj.toString());
            return;
        }

        //检查当前微信号是否被绑定

        //获取openid 查找是否被绑定 以account为准
        String openid=wxMpUser.getOpenId();
        if(openid==null||openid.length()==0){
            //error return
            JSONObject obj=new JSONObject();
            obj.put("code",0);
            obj.put("msg","数据异常，请从微信端正常访问页面！");
            renderJson(obj.toString());
            return;
        }
        Account wxaccount=Account.dao.findByWXID(openid);
        if(wxaccount!=null){
            //微信号已经被绑定 提示解绑
            JSONObject obj=new JSONObject();
            obj.put("code",0);
            obj.put("msg","该微信号已经被绑定，请先前往解绑!");
            renderJson(obj.toString());
            return;
        }

        //正常情况下前面的情况不会出现

        //根据提交的参数 判定绑定账号

        //先确定用户 后执行绑定

        //account 当前用户
        Account account;
        String isLogin=getPara("login");
        if("true".equalsIgnoreCase(isLogin)){
            //登录并绑定
            String  acc=getPara("accountInfo");
            String  psw=getPara("password");

            account=Account.dao.findAccount(acc,psw);

            if(account==null){
                JSONObject obj=new JSONObject();
                obj.put("code",0);
                obj.put("msg","登录失败，请检查用户名及密码!");
                renderJson(obj.toString());
                return;
            }

            setSessionAttr("userInfo",account);

        }
        else{
            //直接绑定
            account=getSessionAttr("userInfo");
        }

        //开始绑定动作
        account.setWeixinId(openid);

        try{

            OauthUser oau=OauthUser.dao.findByOpenId(openid);
            oau.setUserId(account.getUserId());
            //开启事务
            boolean update_res= Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    if(account.update()&&oau.update()){
                       return true;
                    }
                    return false;
                }
            });

            if(update_res){
                //更新session
                setSessionAttr("userInfo",account);

                //设置 WXUser session
                setSessionAttr("WXUser",getSessionAttr("wxMpUser"));

                JSONObject obj=new JSONObject();
                obj.put("code",1);
                obj.put("msg","绑定成功!");

                renderJson(obj.toString());

            }else {
                JSONObject obj=new JSONObject();
                obj.put("code",0);
                obj.put("msg","绑定失败，请重新尝试或联系管理员!");
                renderJson(obj.toString());

            }

        }catch (Exception e){
            e.printStackTrace();
            JSONObject obj=new JSONObject();
            obj.put("code",0);
            obj.put("msg","绑定失败，请重新尝试或联系管理员!");
            renderJson(obj.toString());
        }

    }

    /**
     * 快速解绑微信
     */
    public void quickUnbind(){


    }



}
