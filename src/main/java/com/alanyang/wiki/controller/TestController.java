package com.alanyang.wiki.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//返回字串 or json
@RestController
public class TestController {
//    http://127.0.0.1:8880/hello
//    get,post,put(update), delete
    @RequestMapping("/hello")
    public String hello(){
        return "Hello World";
    }
}
