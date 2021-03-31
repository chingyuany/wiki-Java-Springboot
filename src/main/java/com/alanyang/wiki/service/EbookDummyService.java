package com.alanyang.wiki.service;

import com.alanyang.wiki.mapper.EbookDummyMapperCust;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EbookDummyService {

    @Resource
    private EbookDummyMapperCust ebookDummyMapperCust;

    public void genDummy() {
        ebookDummyMapperCust.genDummy();
    }



}
