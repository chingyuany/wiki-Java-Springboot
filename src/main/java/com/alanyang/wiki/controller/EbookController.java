package com.alanyang.wiki.controller;

import com.alanyang.wiki.req.EbookReq;
import com.alanyang.wiki.resp.CommonResp;
import com.alanyang.wiki.resp.EbookResp;
import com.alanyang.wiki.resp.PageResp;
import com.alanyang.wiki.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

//返回字串 or json
@RestController
//下面所有的get post 的接口路徑都會加上這個前綴ebook
@RequestMapping("/ebook")
public class EbookController {


    @Resource
    private EbookService ebookService;


    @GetMapping("/list")
    public CommonResp list(EbookReq req){
        CommonResp<PageResp<EbookResp>> resp = new CommonResp<>();
        PageResp<EbookResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
}
