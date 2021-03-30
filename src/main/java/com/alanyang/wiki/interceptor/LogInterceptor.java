
package com.alanyang.wiki.interceptor;

import com.alanyang.wiki.resp.UserLoginResp;
import com.alanyang.wiki.util.LoginUserContext;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 /**
  * 需要有 config / SpringMvcConfig.java
  * 拦截器：Spring框架特有的，常用于登录校验，权限校验，请求日志打印 /login
  */
 @Component
 public class LogInterceptor implements HandlerInterceptor {


     private static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);
//
//     @Override
//     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//         // 打印请求信息
//         LOG.info("------------- LogInterceptor start -------------");
//         LOG.info("request address: {} {}", request.getRequestURL().toString(), request.getMethod());
//         LOG.info("remote address: {}", request.getRemoteAddr());
//
//         long startTime = System.currentTimeMillis();
////         requestStartTime 名字可以自己取 但要特殊一點才不會覆蓋其他
//         request.setAttribute("requestStartTime", startTime);
////         要true 才會繼續執行業務邏輯
//         return true;
//     }
@Resource
private RedisTemplate redisTemplate;

     @Override
     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
         // 打印请求信息
         LOG.info("------------- LoginInterceptor Start -------------");
         long startTime = System.currentTimeMillis();
         request.setAttribute("requestStartTime", startTime);

         // OPTIONS请求不做校验,
         // 前后端分离的架构, 前端会发一个OPTIONS请求先做预检, 对预检请求不做校验
         if(request.getMethod().toUpperCase().equals("OPTIONS")){
             return true;
         }

         String path = request.getRequestURL().toString();
         LOG.info("Port login intercept：，request URL path：{}", path);

         //获取header的token参数
         String token = request.getHeader("token");
         LOG.info("Login validation start，token：{}", token);
         if (token == null || token.isEmpty()) {
             LOG.info( "token is empty，request has been intercepted" );
//         error code 401     error msg show at web/src/main.ts
             response.setStatus(HttpStatus.UNAUTHORIZED.value());
             return false;
         }
         Object object = redisTemplate.opsForValue().get(token);
         if (object == null) {
             LOG.warn( "Token invalid，request has been intercepted" );
             response.setStatus(HttpStatus.UNAUTHORIZED.value());
             return false;
         } else {
             LOG.info("Login success：{}", object);
//             放進去Uuser name, actioninterceptor 會去檢查登入的是不是admin
//            store.setUser 在redis存的value就是 userLoginResp 現在把她轉成字串,再轉回 userLoginResp object
             LoginUserContext.setUser(JSON.parseObject((String) object, UserLoginResp.class));
             return true;
         }
     }

     @Override
     public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
         long startTime = (Long) request.getAttribute("requestStartTime");
         LOG.info("------------- LogInterceptor end Elapsed time：{} ms -------------", System.currentTimeMillis() - startTime);
     }
     @Override
     public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        LOG.info("LogInterceptor 结束");
     }
 }
