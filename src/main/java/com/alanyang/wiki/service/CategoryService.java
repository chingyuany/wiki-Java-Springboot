package com.alanyang.wiki.service;

import com.alanyang.wiki.domain.Category;
import com.alanyang.wiki.domain.CategoryExample;
import com.alanyang.wiki.mapper.CategoryMapper;
import com.alanyang.wiki.req.CategoryQueryReq;
import com.alanyang.wiki.req.CategorySaveReq;
import com.alanyang.wiki.resp.CategoryQueryResp;
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
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private SnowFlake snowFlake;

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req){

//        方法實作於 resource/mapper/CategoryMapper.xml
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        //        等於sql where
        CategoryExample.Criteria criteria =  categoryExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%"+req.getName()+"%");
        }
//      這個把sql查詢分頁插件只對第一個SQL作用  先查詢有幾筆資料 然後再查詢3筆 sql limit
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
//      另一個插件 去獲取行數 頁數資料,可返回給前端
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        LOG.info("Total rows：{}", pageInfo.getTotal());
        LOG.info("Total pages：{}", pageInfo.getPages());

//        第一種寫法
//        List<CategoryResp> respList = new ArrayList<>();
//        for (Category category : categoryList) {
//  //            CategoryResp categoryResp = new CategoryResp();
//  //            spring 內建的複製來源物件到目標物件函數
//  //            BeanUtils.copyProperties(category,categoryResp);
//  //            用CopyUtil class的 object 複製
//            CategoryResp categoryResp = CopyUtil.copy(category,CategoryResp.class);
//            respList.add(categoryResp);
//        }

//        用CopyUtil class的列表複製寫法
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);


        PageResp<CategoryQueryResp> pageResp = new PageResp();
//        返回總row number
        pageResp.setTotal(pageInfo.getTotal());
//        返回SQL查詢結果的列表
        pageResp.setList(list);
        return pageResp;
    }
    public List<CategoryQueryResp> all(){

//        方法實作於 resource/mapper/CategoryMapper.xml
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");

        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
//        用CopyUtil class的列表複製寫法
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        return list;
    }

    public void save(CategorySaveReq req){
        Category category = CopyUtil.copy(req,Category.class);
        if (ObjectUtils.isEmpty(req.getId())){
            category.setId(snowFlake.nextId());
//            新增紀錄
            categoryMapper.insert(category);
        }else{
//            更新
            categoryMapper.updateByPrimaryKey(category);
        }

    }
    public void delete(Long id){
        categoryMapper.deleteByPrimaryKey(id);

    }
}
