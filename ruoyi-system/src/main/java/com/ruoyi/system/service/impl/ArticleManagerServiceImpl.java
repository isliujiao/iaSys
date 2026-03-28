package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.Post;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.mapper.PostMapper;
import com.ruoyi.system.service.ArticleManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ArticleManagerServiceImpl implements ArticleManagerService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public int insertPostList(Post post) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        post.setUserId(loginUser.getUserId());
        post.setCreateTime(new Date());
        return postMapper.insertPost(post);
    }
}
