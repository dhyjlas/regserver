package com.thredim.regserver.config;

import com.alibaba.fastjson.JSON;
import com.thredim.regserver.security.AuthUtils;
import com.thredim.regserver.utils.RestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class InterceptorConfig implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(InterceptorConfig.class);

    /**
     * 进入controller层之前拦截请求，身份验证
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o){
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        httpServletResponse.setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "User_token, Content-Type");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true"); //是否支持cookie跨域
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "FileName");

        String token = httpServletRequest.getHeader("User_token") == null ? "" : httpServletRequest.getHeader("User_token").toString();

        try {
            if (AuthUtils.getInstance().cheak(token)) {
                return true;
            } else {
                PrintWriter printWriter = httpServletResponse.getWriter();
                RestResult restResult = RestResult.getFailed("请先通过身份认证");
                restResult.setCode("403");
                printWriter.write(JSON.toJSONString(restResult));
                printWriter.flush();
                printWriter.close();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
