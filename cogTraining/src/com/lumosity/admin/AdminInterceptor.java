package com.lumosity.admin;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class AdminInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        //检查客户端
        String agent=inv.getController().getRequest().getHeader("User-Agent").toLowerCase();
        if(agent!=null&&agent.indexOf("micromessenger")>-1){
            inv.invoke();

        }else{
            inv.getController().setAttr("resultStr","请从微信端正常访问页面！");
            inv.getController().render("result.html");
        }
    }
}
