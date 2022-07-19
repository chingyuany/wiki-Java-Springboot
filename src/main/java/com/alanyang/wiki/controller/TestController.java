package com.alanyang.wiki.controller;

import com.alanyang.wiki.domain.Test;
import com.alanyang.wiki.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

//返回字串 or json
@RestController
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);
    @Resource
    private RedisTemplate redisTemplate;

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


    @RequestMapping("/redis/set/{key}/{value}")
    public String set(@PathVariable Long key, @PathVariable String value) {
        redisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
        LOG.info("key: {}, value: {}", key, value);
        return "success";
    }

    @RequestMapping("/redis/get/{key}")
    public Object get(@PathVariable String key) {
        Object object = redisTemplate.opsForValue().get(key);
        LOG.info("key: {}, value: {}", key, object);
        return object;
    }
    @RequestMapping("/redis/delete/{key}")
    public Object delete(@PathVariable String key) {
        Object object = redisTemplate.delete(key);;
        LOG.info("key: {}, value: {}", key, object);
        return object;
    }
    @RequestMapping("/redis/deleteall/{ip}")
    public Object deleteall(@PathVariable String ip) {
        Object object = Object.class;

        for(int i = 1; i <=6;i++){
            String key = "DOC_VOTE_"+i+"_"+ip;
            object = redisTemplate.delete(key);;
            LOG.info("key: {}, value: {}", key, object);
        }


        return object;
    }
}
