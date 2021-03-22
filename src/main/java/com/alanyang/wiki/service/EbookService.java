package com.alanyang.wiki.service;

import com.alanyang.wiki.domain.Ebook;
import com.alanyang.wiki.mapper.EbookMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;
    public List<Ebook> list(){
//        方法實作於 resource/mapper/EbookMapper.xml
        return ebookMapper.selectByExample(null);
    }
}
