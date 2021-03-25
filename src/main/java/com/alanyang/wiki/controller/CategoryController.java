package com.alanyang.wiki.controller;

import com.alanyang.wiki.req.CategoryQueryReq;
import com.alanyang.wiki.req.CategorySaveReq;
import com.alanyang.wiki.resp.CommonResp;
import com.alanyang.wiki.resp.CategoryQueryResp;
import com.alanyang.wiki.resp.PageResp;
import com.alanyang.wiki.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

//返回字串 or json
@RestController
//下面所有的get post 的接口路徑都會加上這個前綴category
@RequestMapping("/category")
public class CategoryController {


    @Resource
    private CategoryService categoryService;


    @GetMapping("/list")
//    @valid  開啟驗證規則 在pagereq 裡面
    public CommonResp list(@Valid CategoryQueryReq req){
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryService.list(req);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/save")
//    commonresp 回傳True而已 new出來一定是true
//    如果是Json格式post 一定要加上@RequestBody,如果是form post 就不需要
    public CommonResp save(@Valid @RequestBody CategorySaveReq req){
        CommonResp resp = new CommonResp<>();
        categoryService.save(req);

        return resp;
    }
//    {}是指動態  pathvariable 可以獲取url上的參數
    @DeleteMapping ("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        categoryService.delete(id);
        return resp;
    }
}
