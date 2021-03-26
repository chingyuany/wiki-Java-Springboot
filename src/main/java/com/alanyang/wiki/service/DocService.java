package com.alanyang.wiki.service;

import com.alanyang.wiki.domain.Doc;
import com.alanyang.wiki.domain.DocExample;
import com.alanyang.wiki.mapper.DocMapper;
import com.alanyang.wiki.req.DocQueryReq;
import com.alanyang.wiki.req.DocSaveReq;
import com.alanyang.wiki.resp.DocQueryResp;
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
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);
    @Resource
    private DocMapper docMapper;
    @Resource
    private SnowFlake snowFlake;

    public PageResp<DocQueryResp> list(DocQueryReq req){

//        方法實作於 resource/mapper/DocMapper.xml
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        //        等於sql where
        DocExample.Criteria criteria =  docExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%"+req.getName()+"%");
        }
//      這個把sql查詢分頁插件只對第一個SQL作用  先查詢有幾筆資料 然後再查詢3筆 sql limit
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);
//      另一個插件 去獲取行數 頁數資料,可返回給前端
        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("Total rows：{}", pageInfo.getTotal());
        LOG.info("Total pages：{}", pageInfo.getPages());

//        第一種寫法
//        List<DocResp> respList = new ArrayList<>();
//        for (Doc doc : docList) {
//  //            DocResp docResp = new DocResp();
//  //            spring 內建的複製來源物件到目標物件函數
//  //            BeanUtils.copyProperties(doc,docResp);
//  //            用CopyUtil class的 object 複製
//            DocResp docResp = CopyUtil.copy(doc,DocResp.class);
//            respList.add(docResp);
//        }

//        用CopyUtil class的列表複製寫法
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);


        PageResp<DocQueryResp> pageResp = new PageResp();
//        返回總row number
        pageResp.setTotal(pageInfo.getTotal());
//        返回SQL查詢結果的列表
        pageResp.setList(list);
        return pageResp;
    }
    public List<DocQueryResp> all(){

//        方法實作於 resource/mapper/DocMapper.xml
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");

        List<Doc> docList = docMapper.selectByExample(docExample);
//        用CopyUtil class的列表複製寫法
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    public void save(DocSaveReq req){
        Doc doc = CopyUtil.copy(req,Doc.class);
        if (ObjectUtils.isEmpty(req.getId())){
            doc.setId(snowFlake.nextId());
//            新增紀錄
            docMapper.insert(doc);
        }else{
//            更新
            docMapper.updateByPrimaryKey(doc);
        }

    }
    public void delete(Long id){
        docMapper.deleteByPrimaryKey(id);

    }
    public void delete(List<String> ids){
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria =  docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);

    }
}
