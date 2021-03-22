package com.alanyang.wiki.controller;

import com.alanyang.wiki.domain.Test;
import com.alanyang.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

//返回字串 or json
@RestController
public class TestController {
//    如果讀不到application properties裡面配置項的值 就會輸出預設值Test
    @Value("${test.hello:Test}")
    private String testhello;

    @Resource
    private TestService testService;


//    http://127.0.0.1:8880/hello
//    get,post,put(update), delete
    @GetMapping("/hello")
    public String hello(){
        return "Hello World" + testhello;
    }

    @PostMapping("/hello/post")
    public String helloPost(String name){
        return "Hello World,Post," + name;
    }

    @GetMapping("/test/list")
    public List<Test> list(){
        return testService.list();
    }
}
