package com.lumosity.games;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * 客户端类型拦截器 拦截移动端访问游戏页
 */
public class GameClientInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation invocation) {

        String[] agents={"Android", "iPhone", "iPod","iPad", "Windows Phone", "MQQBrowser","micromessenger"};
        String ua=invocation.getController().getRequest().getHeader("User-Agent").toLowerCase();
        if(ua==null){
            invocation.invoke();
        }
        else{

            boolean mobileAgent=false;
            for (String agent:agents) {
                if(ua.indexOf(agent.toLowerCase())>-1){
                    mobileAgent=true;
                    break;
                }
            }
            if(mobileAgent){
                //移动端
                invocation.getController().render("/games/play_access_deny.html");
            }
            else{
                invocation.invoke();
            }
        }

    }
}
