package com.lumosity.admin;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.lumosity.login.oauth.meta.ShareLoginDict;
import net.sf.ehcache.search.Result;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TokenThread implements Runnable {
    public static Connection connection;


    @Override
    public void run() {

      //  System.out.println("access token thread run!");
        while (true){

            try{

                //开始获取access_token
                String appid=ShareLoginDict.weixinMpAppid;
                String appsecret=ShareLoginDict.WeixinMpAppSecret;
                //请求微信服务器获取AccessToken
                String tokenUrl=String.format("%s?grant_type=client_credential&appid=%s&secret=%s", ShareLoginDict.WXAccessTokenAddr,appid,appsecret);
                String resultStr= HttpKit.get(tokenUrl);
                if(resultStr==null||resultStr.length()==0){
                    throw new Exception();
                }
                JSONObject tokenObj=new JSONObject(resultStr);
                if(tokenObj.has("access_token")){
                    String accessToken=tokenObj.getString("access_token");
                    long token_expires_in=tokenObj.getInt("expires_in");

                    //开始请求ticket
                    String ticketUrl=String.format("%s?access_token=%s&type=jsapi",ShareLoginDict.WX_JSAPI_TICKET_ADDR,accessToken);
                    String responseStr=HttpKit.get(ticketUrl);
                    if(responseStr==null||responseStr.length()==0){
                        throw  new  Exception();
                    }
                    JSONObject ticketObj=new JSONObject(responseStr);

                    String ticket;
                    long ticket_expires_in;
                    if(ticketObj.getInt("errcode")==0){
                        ticket=ticketObj.getString("ticket");
                        ticket_expires_in=ticketObj.getInt("expires_in");
                    }
                    else{
                        throw  new Exception();
                    }

                    PropKit.use("druid.properties");
                    connection= DriverManager.getConnection(PropKit.get("jdbcUrl"), PropKit.get("user"),
                            PropKit.get("password").trim());
                    PreparedStatement pstmt=null;
                    //查看是否有对应的配置
                    String selectSql="select * from wx_app_config where appid=?";
                    pstmt=connection.prepareStatement(selectSql);
                    pstmt.setString(1,appid);
                    ResultSet rSet=pstmt.executeQuery();
                    if(rSet.next()){
                        //存在配置 开始更新
                        String updateSql="update wx_app_config set access_token =?, access_token_expires=?,api_ticket=?,api_ticket_expires=? " +
                                "where appid=?";
                        PreparedStatement ps=connection.prepareStatement(updateSql);
                        ps.setString(1,accessToken);

                        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date token_e=new Date(new Date().getTime()+token_expires_in*1000);
                        ps.setString(2,format.format(token_e));

                        ps.setString(3,ticket);
                        Date ticket_e=new Date(new Date().getTime()+ticket_expires_in*1000);

                        ps.setString(4,format.format(ticket_e));
                        ps.setString(5,appid);
                        boolean rest=ps.execute();

                        long expires_in=token_expires_in;
                         if(token_expires_in>ticket_expires_in){
                             expires_in=ticket_expires_in;
                         }
                         connection.close();
                         Thread.sleep((expires_in-200)*1000);

                    }else{
                        //不存在配置 开始插入
                        String insertSql="insert into wx_app_config(appid,appsecret,access_token,access_token_expires,api_ticket,api_ticket_expires) " +
                                " values(?,?,?,?,?,?)";
                        PreparedStatement ps=connection.prepareStatement(insertSql);
                        ps.setString(1,appid);
                        ps.setString(2,appsecret);
                        ps.setString(3,accessToken);

                        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date token_e=new Date(new Date().getTime()+token_expires_in*1000);
                        ps.setString(4,format.format(token_e));


                        ps.setString(5,ticket);
                        Date ticket_e=new Date(new Date().getTime()+ticket_expires_in*1000);
                        ps.setString(6,format.format(ticket_e));
                       boolean rest=ps.execute();

                       long expires_in=token_expires_in;
                       if(token_expires_in>ticket_expires_in){
                            expires_in=ticket_expires_in;
                       }
                       connection.close();
                       Thread.sleep((expires_in-200)*1000);


                    }

                }
                else{
                    throw new Exception();
                }

            }catch (Exception e){
                e.printStackTrace();
                try{
                    if(connection!=null){
                        connection.close();
                    }
                    //1分钟后再刷新
                    Thread.sleep(60*1000);


                }catch (Exception e2){
                //    e2.printStackTrace();
                }
            }
        }
    }
}
