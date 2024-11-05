package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.ErrorCode;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.SearchTypeEnum;
import com.ruoyi.common.exception.base.BusinessException;
import com.ruoyi.system.datasource.*;
import com.ruoyi.system.domain.dto.search.SearchRequest;
import com.ruoyi.system.domain.vo.SearchVO;
import com.ruoyi.system.service.PostService;
import com.ruoyi.system.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Author
 * @Date 2023/11/17 10:10
 * @Description
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {
    @Autowired
    private DataSourceRegistry dataSourceRegistry;

    @Autowired
    private UserDataSource userDataSource;

    @Autowired
    private PostDataSource postDataSource;

    @Autowired
    private PictureDataSource pictureDataSource;

    @Autowired
    private PostService postService;

    /**
     * 查询相关数据
     *
     * @param searchRequest 查询条件
     * @param request       http相关信息
     * @return
     */
    @Override
    public SearchVO searchAll(SearchRequest searchRequest, HttpServletRequest request) {
        String type = searchRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
//        if(null == searchTypeEnum) {
//            throw new ServiceException("搜索类型不存在");
//        }

        String searchText = searchRequest.getSearchText();
//        long current = searchRequest.getCurrent();
//        long pageSize = searchRequest.getPageSize();

        //如果没有搜索类型，则查出全部信息
        if (null == searchTypeEnum) {
            CompletableFuture<TableDataInfo> userTask = CompletableFuture.supplyAsync(() -> {
                //用户
                return userDataSource.doSearch(searchText);
            });
            CompletableFuture<TableDataInfo> postTask = CompletableFuture.supplyAsync(() -> {
                //文章
                return postDataSource.doSearch(searchText);
            });
            CompletableFuture<TableDataInfo> pictureTask = CompletableFuture.supplyAsync(() -> {
                //图片
                return pictureDataSource.doSearch(searchText);
            });

            //聚合，等待以上任务并行执行完毕后继续执行下面操作
            CompletableFuture.allOf(userTask, postTask, pictureTask).join();
            try {
                TableDataInfo userVoPage = userTask.get();
                TableDataInfo postVoPage = postTask.get();
                TableDataInfo picturePage = pictureTask.get();

                SearchVO searchVO = new SearchVO();
                searchVO.setSysUser(userVoPage);
                searchVO.setPostList(postVoPage);
                searchVO.setPictureList(picturePage);
                return searchVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        } else {
            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource = dataSourceRegistry.getDataSourceByType(type);
            searchVO.setDataList(dataSource.doSearch(searchText));
            return searchVO;
        }
    }
}
