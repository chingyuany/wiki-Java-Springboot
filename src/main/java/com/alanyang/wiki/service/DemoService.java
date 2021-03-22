package com.alanyang.wiki.service;

import com.alanyang.wiki.domain.Demo;
import com.alanyang.wiki.mapper.DemoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DemoService {
    @Resource
    private DemoMapper demoMapper;
    public List<Demo> list(){
//        方法實作於 resource/mapper/DemoMapper.xml
        return demoMapper.selectByExample(null);
    }
}
