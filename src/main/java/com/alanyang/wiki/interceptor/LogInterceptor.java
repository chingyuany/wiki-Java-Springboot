package com.alanyang.wiki.interceptor;

 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.stereotype.Component;
 import org.springframework.web.servlet.HandlerInterceptor;
 import org.springframework.web.servlet.ModelAndView;

 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;

 /**
  * 需要有 config / SpringMvcConfig.java
  * 拦截器：Spring框架特有的，常用于登录校验，权限校验，请求日志打印 /login
  */
 @Component
 public class LogInterceptor implements HandlerInterceptor {

     private static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

     @Override
     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
         // 打印请求信息
         LOG.info("------------- LogInterceptor start -------------");
         LOG.info("request address: {} {}", request.getRequestURL().toString(), request.getMethod());
         LOG.info("remote address: {}", request.getRemoteAddr());

         long startTime = System.currentTimeMillis();
//         requestStartTime 名字可以自己取 但要特殊一點才不會覆蓋其他
         request.setAttribute("requestStartTime", startTime);
//         要true 才會繼續執行業務邏輯
         return true;
     }

     @Override
     public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
         long startTime = (Long) request.getAttribute("requestStartTime");
         LOG.info("------------- LogInterceptor end Elapsed time：{} ms -------------", System.currentTimeMillis() - startTime);
     }
 }
