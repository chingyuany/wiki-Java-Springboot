package com.alanyang.wiki.controller;

import com.alanyang.wiki.req.EbookQueryReq;
import com.alanyang.wiki.req.EbookSaveReq;
import com.alanyang.wiki.resp.CommonResp;
import com.alanyang.wiki.resp.EbookQueryResp;
import com.alanyang.wiki.resp.PageResp;
import com.alanyang.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

//返回字串 or json
@RestController
//下面所有的get post 的接口路徑都會加上這個前綴ebook
@RequestMapping("/ebook")
public class EbookController {


    @Resource
    private EbookService ebookService;


    @GetMapping("/list")
    public CommonResp list(EbookQueryReq req){
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/save")
//    commonresp 回傳True而已 new出來一定是true
//    如果是Json格式post 一定要加上@RequestBody,如果是form post 就不需要
    public CommonResp save(@RequestBody EbookSaveReq req){
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);

        return resp;
    }
}
