package com.alanyang.wiki.service;

import com.alanyang.wiki.domain.Ebook;
import com.alanyang.wiki.domain.EbookExample;
import com.alanyang.wiki.mapper.EbookMapper;
import com.alanyang.wiki.req.EbookQueryReq;
import com.alanyang.wiki.req.EbookSaveReq;
import com.alanyang.wiki.resp.EbookQueryResp;
import com.alanyang.wiki.resp.PageResp;
import com.alanyang.wiki.util.CopyUtil;
import com.alanyang.wiki.util.SnowFlake;
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
    @Resource
    private SnowFlake snowFlake;

    public PageResp<EbookQueryResp> list(EbookQueryReq req){

//        方法實作於 resource/mapper/EbookMapper.xml
        EbookExample ebookExample = new EbookExample();
        //        等於sql where
        EbookExample.Criteria criteria =  ebookExample.createCriteria();
//        有傳name參數進來 才去模糊搜索
        if (!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%"+req.getName()+"%");
        }
//      這個把sql查詢分頁插件只對第一個SQL作用  先查詢有幾筆資料 然後再查詢3筆 sql limit
        PageHelper.startPage(req.getPage(), req.getSize());
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
        List<EbookQueryResp> list = CopyUtil.copyList(ebookList, EbookQueryResp.class);


        PageResp<EbookQueryResp> pageResp = new PageResp();
//        返回總row number
        pageResp.setTotal(pageInfo.getTotal());
//        返回SQL查詢結果的列表
        pageResp.setList(list);
        return pageResp;
    }

    public void save(EbookSaveReq req){
        Ebook ebook = CopyUtil.copy(req,Ebook.class);
        if (ObjectUtils.isEmpty(req.getId())){
            ebook.setId(snowFlake.nextId());
//            新增紀錄
            ebookMapper.insert(ebook);
        }else{
//            更新
            ebookMapper.updateByPrimaryKey(ebook);
        }

    }
    public void delete(Long id){
        ebookMapper.deleteByPrimaryKey(id);

    }
}
