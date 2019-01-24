package com.lumosity.admin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * 用于刷新 微信access_token 和 jsapi_ticket
 */
public class InitServlet extends HttpServlet {
    public void init() throws ServletException{
        new Thread(new TokenThread()).start();
    }
}
