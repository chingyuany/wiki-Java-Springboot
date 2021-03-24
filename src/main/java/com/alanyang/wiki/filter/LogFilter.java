/*
package com.alanyang.wiki.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
// servlet 容器
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class LogFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 打印请求信息
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        LOG.info("------------- LogFilter start-------------");
//          requesturl 接口地址  ,  getmethod = post or get
        LOG.info("request address: {} {}", request.getRequestURL().toString(), request.getMethod());
//         從哪一個ip訪問進來的
        LOG.info("remote address: {}", request.getRemoteAddr());

        long startTime = System.currentTimeMillis();
//         繼續跑其他過濾器
        filterChain.doFilter(servletRequest, servletResponse);
        LOG.info("------------- LogFilter end Elapsed time：{} ms -------------", System.currentTimeMillis() - startTime);
    }
}
*/