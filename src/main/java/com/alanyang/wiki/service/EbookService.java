package com.alanyang.wiki.service;

import com.alanyang.wiki.domain.Ebook;
import com.alanyang.wiki.domain.EbookExample;
import com.alanyang.wiki.mapper.EbookMapper;
import com.alanyang.wiki.req.EbookReq;
import com.alanyang.wiki.resp.EbookResp;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {
    @Resource
    private EbookMapper ebookMapper;
    public List<EbookResp> list(EbookReq req){
//        方法實作於 resource/mapper/EbookMapper.xml
        EbookExample ebookExample = new EbookExample();
        //        等於sql where
        EbookExample.Criteria criteria =  ebookExample.createCriteria();
        criteria.andNameLike("%"+req.getName()+"%");
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        List<EbookResp> respList = new ArrayList<>();
        for (Ebook ebook : ebookList) {
            EbookResp ebookResp = new EbookResp();
//            spring 內建的複製來源物件到目標物件函數
            BeanUtils.copyProperties(ebook,ebookResp);
            respList.add(ebookResp);
        }
        return respList;

    }
}
