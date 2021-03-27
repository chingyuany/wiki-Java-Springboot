package com.alanyang.wiki.controller;

import com.alanyang.wiki.req.UserQueryReq;
import com.alanyang.wiki.req.UserSaveReq;
import com.alanyang.wiki.resp.CommonResp;
import com.alanyang.wiki.resp.PageResp;
import com.alanyang.wiki.resp.UserQueryResp;
import com.alanyang.wiki.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

//返回字串 or json
@RestController
//下面所有的get post 的接口路徑都會加上這個前綴user
@RequestMapping("/user")
public class UserController {


    @Resource
    private UserService userService;


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
}
