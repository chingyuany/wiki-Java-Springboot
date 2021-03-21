package com.alanyang.wiki.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

//返回字串 or json
@RestController
public class TestController {
//    http://127.0.0.1:8880/hello
//    get,post,put(update), delete
    @GetMapping("/hello")
    public String hello(){
        return "Hello World";
    }

    @PostMapping("/hello/post")
    public String helloPost(String name){
        return "Hello World,Post," + name;
    }
}
