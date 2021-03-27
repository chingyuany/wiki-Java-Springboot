package com.alanyang.wiki.controller;

import com.alanyang.wiki.req.UserLoginReq;
import com.alanyang.wiki.req.UserQueryReq;
import com.alanyang.wiki.req.UserResetPasswordReq;
import com.alanyang.wiki.req.UserSaveReq;
import com.alanyang.wiki.resp.CommonResp;
import com.alanyang.wiki.resp.PageResp;
import com.alanyang.wiki.resp.UserLoginResp;
import com.alanyang.wiki.resp.UserQueryResp;
import com.alanyang.wiki.service.UserService;
import com.alanyang.wiki.util.SnowFlake;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;


//返回字串 or json
@RestController
//下面所有的get post 的接口路徑都會加上這個前綴user
@RequestMapping("/user")
public class UserController {


    @Resource
    private UserService userService;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private RedisTemplate redisTemplate;


    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/list")
//    @valid  開啟驗證規則 在pagereq 裡面
    public CommonResp list(@Valid UserQueryReq req){
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/save")
//    commonresp 回傳True而已 new出來一定是true
//    如果是Json格式post 一定要加上@RequestBody,如果是form post 就不需要
    public CommonResp save(@Valid @RequestBody UserSaveReq req){
//        加密成md5 32bit 16進位字符串hex
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }
//    {}是指動態  pathvariable 可以獲取url上的參數
    @DeleteMapping ("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

    @PostMapping("/reset-password")
//    commonresp 回傳True而已 new出來一定是true
//    如果是Json格式post 一定要加上@RequestBody,如果是form post 就不需要
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req){
//        加密成md5 32bit 16進位字符串hex
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }
    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req){
//        加密成md5 32bit 16進位字符串hex
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);

        Long token = snowFlake.nextId();
        LOG.info("生成單點登入token: {} 放入redis",token);

        userLoginResp.setToken(token.toString());
//        ops = operation  set 插入一個set(key,value,有效期限,時間單位)
//        class要遠程傳輸 需要轉成序列化 才能再還原回來  JSONObject.toJSONString 9-9
        redisTemplate.opsForValue().set(token, JSONObject.toJSONString(userLoginResp), 3600 * 24, TimeUnit.SECONDS);
        resp.setContent(userLoginResp);
        return resp;
    }
}
