package com.lumosity.admin;


import com.jfinal.kit.HttpKit;
import com.lumosity.login.oauth.meta.ShareLoginDict;
import com.lumosity.model.WxAppConfig;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import java.util.Date;

public class WxAppService {
    public static WxAppService me=new WxAppService();

    /**
     *
     * 已经配置了自动刷新线程 正常情况下只会 取得未过期的token
     * 获取或刷新微信公众号access_token
     *
     * @return
     */
    public String getAccessToken(){

        try{
            String appid=ShareLoginDict.weixinMpAppid;
            WxAppConfig wxAppConfig=WxAppConfig.dao.findByAppid(appid);
            //数据库里没有对应配置
            if(wxAppConfig==null){
                if(creatMpConfig()){
                    //添加成功
                    wxAppConfig=WxAppConfig.dao.findByAppid(appid);
                }
                else{
                    //出错 返回
                    return  null;
                }
            }
            Date Now=new Date();
            Date Expires=wxAppConfig.getAccessTokenExpires();
            //没有过期
            if(Expires!=null&&Expires.getTime()-Now.getTime()>5*60*1000){
                return wxAppConfig.getAccessToken();
            }
            else{
                //请求微信服务器获取AccessToken
                String url=String.format("%s?grant_type=client_credential&appid=%s&secret=%s", ShareLoginDict.WXAccessTokenAddr,wxAppConfig.getAppid(),wxAppConfig.getAppsecret());
                String resultStr= HttpKit.get(url);
                if(resultStr==null||resultStr.length()==0){
                    return null;
                }
                JSONObject obj=new JSONObject(resultStr);
                if(obj.has("access_token")){
                    String accessToken=obj.getString("access_token");
                    wxAppConfig.setAccessToken(accessToken);
                    long expires_in=obj.getInt("expires_in");
                    long expires=new Date().getTime()+expires_in*1000;
                    wxAppConfig.setAccessTokenExpires(new Date(expires));
                    if(wxAppConfig.update()){
                        return  accessToken;
                    }
                    else{
                        return  null;
                    }

                }
                else{
                    return null;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }



    }


    /**
     * 向数据库插入公众号配置
     * @return
     */
    public boolean creatMpConfig(){
        try{
            WxAppConfig wxAppConfig=new WxAppConfig();
            wxAppConfig.setAppid(ShareLoginDict.weixinMpAppid);
            wxAppConfig.setAppsecret(ShareLoginDict.WeixinMpAppSecret);
            return wxAppConfig.save();
        }
        catch (Exception e){
       //     e.printStackTrace();
            return  false;
        }


    }

    /**
     * 获取微信JSAPI_TICKET
     */
    public String getJsapiTicket(){

        //直接从数据库取
        String appid=ShareLoginDict.weixinMpAppid;
        WxAppConfig wxAppConfig=WxAppConfig.dao.findByAppid(appid);
        if(wxAppConfig!=null){
            Date Now=new Date();
            Date Expires=wxAppConfig.getApiTicketExpires();
            if(Expires!=null&&Expires.getTime()-Now.getTime()>5*60*1000){
                //没有过期 直接取出来用
                return wxAppConfig.getApiTicket();
            }
            else{
                //过期
                return refreshJsapiTicket();
            }
        }else{
            return  refreshJsapiTicket();
        }



    }

    /**
     * 发送请求刷新 ticket
     */
    private String  refreshJsapiTicket(){
        String accessToken=getAccessToken();
        WxAppConfig wxAppConfig=WxAppConfig.dao.findByAppid(ShareLoginDict.weixinMpAppid);
        if(accessToken==null||accessToken.length()==0){
            return  null;
        }
        String url=String.format("%s?access_token=%s&type=jsapi",ShareLoginDict.WX_JSAPI_TICKET_ADDR,accessToken);
        String responseStr=HttpKit.get(url);
        if(responseStr==null||responseStr.length()==0){
            return null;
        }
        JSONObject obj=new JSONObject(responseStr);
        if(obj.getInt("errcode")==0){
            String ticket=obj.getString("ticket");
            wxAppConfig.setApiTicket(ticket);
            long expires_in=obj.getInt("expires_in");
            long expires=new Date().getTime()+expires_in*1000;
            wxAppConfig.setApiTicketExpires(new Date(expires));

            wxAppConfig.update();
            return  ticket;
        }
        else{

            return  null;
        }
    }

    /**
     * 调用 微信JS所需的签名
     * @param nocestr
     * @param timestamp
     * @param url
     * @return
     */
    public String getJSAPISignature(String nocestr,String timestamp,String url){
        String ticket=getJsapiTicket();

        if(ticket==null||ticket.length()==0){
            return  null;
        }
        String originStr=String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%s&url=%s",
                ticket,
                nocestr,
                timestamp,
                url);

       // System.out.println("originStr:"+originStr+"\r\n");
        return DigestUtils.sha1Hex(originStr);

    }



}
