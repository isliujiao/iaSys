package com.ruoyi.system.datasource;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.system.domain.dto.post.PostQueryRequest;
import com.ruoyi.system.domain.vo.PostVO;
import com.ruoyi.system.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 帖子服务实现
 */
@Service
@Slf4j
public class PostDataSource extends BaseController implements DataSource<PostVO> {

    @Resource
    private PostService postService;

    @Override
    public TableDataInfo doSearch(String searchText) {
        startPage();
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        postQueryRequest.setCurrent(pageDomain.getPageNum());
        postQueryRequest.setPageSize(pageDomain.getPageSize());
        return getDataTable(postService.searchFromEs(postQueryRequest));
    }

    //    @Override
//    public Page<PostVO> doSearch(String searchText, long pageNum, long pageSize) {
//        PostQueryRequest postQueryRequest = new PostQueryRequest();
//        postQueryRequest.setSearchText(searchText);
//        postQueryRequest.setCurrent(pageNum);
//        postQueryRequest.setPageSize(pageSize);
//        ServletRequestAttributes servletRequestAttributes =
//                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        assert servletRequestAttributes != null;
//        HttpServletRequest request = servletRequestAttributes.getRequest();
//        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
//        return postService.getPostVOPage(postPage, request);
//    }

}




