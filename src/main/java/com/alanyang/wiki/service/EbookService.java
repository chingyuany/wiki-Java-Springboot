package com.alanyang.wiki.service;
import com.alanyang.wiki.domain.Ebook;
import com.alanyang.wiki.domain.EbookExample;
import com.alanyang.wiki.mapper.EbookMapper;
import com.alanyang.wiki.req.EbookReq;
import com.alanyang.wiki.resp.EbookResp;
import com.alanyang.wiki.util.CopyUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);
    @Resource
    private EbookMapper ebookMapper;
    public List<EbookResp> list(EbookReq req){

//        方法實作於 resource/mapper/EbookMapper.xml
        EbookExample ebookExample = new EbookExample();
        //        等於sql where
        EbookExample.Criteria criteria =  ebookExample.createCriteria();
//        有傳name參數進來 才去模糊搜索
        if (!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%"+req.getName()+"%");
        }
//      這個把sql查詢分頁插件只對第一個SQL作用  先查詢有幾筆資料 然後再查詢3筆 sql limit
        PageHelper.startPage(1,3);
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
//      另一個插件 去獲取行數 頁數資料,可返回給前端
        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("Total rows：{}", pageInfo.getTotal());
        LOG.info("Total pages：{}", pageInfo.getPages());

//        第一種寫法
//        List<EbookResp> respList = new ArrayList<>();
//        for (Ebook ebook : ebookList) {
//  //            EbookResp ebookResp = new EbookResp();
//  //            spring 內建的複製來源物件到目標物件函數
//  //            BeanUtils.copyProperties(ebook,ebookResp);
//  //            用CopyUtil class的 object 複製
//            EbookResp ebookResp = CopyUtil.copy(ebook,EbookResp.class);
//            respList.add(ebookResp);
//        }

//        用CopyUtil class的列表複製寫法
        List<EbookResp> respList = CopyUtil.copyList(ebookList,EbookResp.class);


        return respList;

    }
}
