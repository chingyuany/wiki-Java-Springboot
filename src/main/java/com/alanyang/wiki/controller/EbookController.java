package com.alanyang.wiki.controller;

import com.alanyang.wiki.domain.Ebook;
import com.alanyang.wiki.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

//返回字串 or json
@RestController
//下面所有的get post 的接口路徑都會加上這個前綴ebook
@RequestMapping("/ebook")
public class EbookController {


    @Resource
    private EbookService ebookService;


    @GetMapping("/list")
    public List<Ebook> list(){
        return ebookService.list();
    }
}
