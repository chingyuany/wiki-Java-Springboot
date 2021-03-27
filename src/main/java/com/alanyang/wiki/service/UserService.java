package com.alanyang.wiki.service;

import com.alanyang.wiki.domain.User;
import com.alanyang.wiki.domain.UserExample;
import com.alanyang.wiki.mapper.UserMapper;
import com.alanyang.wiki.req.UserQueryReq;
import com.alanyang.wiki.req.UserSaveReq;
import com.alanyang.wiki.resp.UserQueryResp;
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
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    @Resource
    private UserMapper userMapper;
    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req){

//        方法實作於 resource/mapper/UserMapper.xml
        UserExample userExample = new UserExample();
        //        等於sql where
        UserExample.Criteria criteria =  userExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getLoginName())){
            criteria.andLoginNameLike("%"+req.getLoginName()+"%");
        }

//      這個把sql查詢分頁插件只對第一個SQL作用  先查詢有幾筆資料 然後再查詢3筆 sql limit
        PageHelper.startPage(req.getPage(), req.getSize());
        List<User> userList = userMapper.selectByExample(userExample);
//      另一個插件 去獲取行數 頁數資料,可返回給前端
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("Total rows：{}", pageInfo.getTotal());
        LOG.info("Total pages：{}", pageInfo.getPages());

//        第一種寫法
//        List<UserResp> respList = new ArrayList<>();
//        for (User user : userList) {
//  //            UserResp userResp = new UserResp();
//  //            spring 內建的複製來源物件到目標物件函數
//  //            BeanUtils.copyProperties(user,userResp);
//  //            用CopyUtil class的 object 複製
//            UserResp userResp = CopyUtil.copy(user,UserResp.class);
//            respList.add(userResp);
//        }

//        用CopyUtil class的列表複製寫法
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);


        PageResp<UserQueryResp> pageResp = new PageResp();
//        返回總row number
        pageResp.setTotal(pageInfo.getTotal());
//        返回SQL查詢結果的列表
        pageResp.setList(list);
        return pageResp;
    }

    public void save(UserSaveReq req){
        User user = CopyUtil.copy(req,User.class);
        if (ObjectUtils.isEmpty(req.getId())){
            user.setId(snowFlake.nextId());
//            新增紀錄
            userMapper.insert(user);
        }else{
//            更新
            userMapper.updateByPrimaryKey(user);
        }

    }
    public void delete(Long id){
        userMapper.deleteByPrimaryKey(id);

    }
}
