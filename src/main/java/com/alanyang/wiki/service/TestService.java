package com.alanyang.wiki.service;

import com.alanyang.wiki.domain.Test;
import com.alanyang.wiki.mapper.TestMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TestService {
    @Resource
    private TestMapper testMapper;
    public List<Test> list(){
//        方法實作於 resource/mapper/TestMapper.xml
        return testMapper.list();
    }
}
