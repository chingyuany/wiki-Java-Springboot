package com.alanyang.wiki.interceptor;

import com.alanyang.wiki.resp.CommonResp;
import com.alanyang.wiki.resp.UserLoginResp;
import com.alanyang.wiki.util.LoginUserContext;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 拦截器：Spring框架特有的，常用于登录校验，权限校验，请求日志打印
 */
@Component
public class ActionInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ActionInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // OPTIONS请求不做校验,
        // 前后端分离的架构, 前端会发一个OPTIONS请求先做预检, 对预检请求不做校验
        if("OPTIONS".equals(request.getMethod().toUpperCase())){
            return true;
        }


        UserLoginResp userLoginResp = LoginUserContext.getUser();
        if ("admin".equals(userLoginResp.getLoginName())) {
            // admin用户不拦截
            return true;
        }

        LOG.info("Operate has been intercepted");
//        搭配前端網頁        message.error(data.message);
        response.setStatus(HttpStatus.OK.value());
        CommonResp commonResp = new CommonResp();
        commonResp.setSuccess(false);
        commonResp.setMessage("Only admin user can add,modify,delete data");
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(JSONObject.toJSON(commonResp));
        return false;
    }

}
