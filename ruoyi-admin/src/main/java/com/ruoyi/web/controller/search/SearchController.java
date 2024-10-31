package com.ruoyi.web.controller.search;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.system.domain.dto.post.PostEsDTO;
import com.ruoyi.system.domain.dto.search.SearchRequest;
import com.ruoyi.system.service.PostService;
import com.ruoyi.system.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController extends BaseController {


    @Resource
    private SearchService searchService;

    @Resource
    private PostService postService;

    @GetMapping("/all")
    public AjaxResult searchAll(SearchRequest searchRequest, HttpServletRequest request) {
        return success(searchService.searchAll(searchRequest, request));
    }

    /**
     * 查询电子书前5的文章
     */
    @GetMapping("/hot")
    public AjaxResult searchHot() {
        return success(searchService.searchTopFive());
    }


    /**
     * 根据文章ID推荐相关文章
     *
     * @param id
     * @return
     */
    @GetMapping("/recommend/{id}")
    public TableDataInfo recommend(@PathVariable Long id) {
        startPage();
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageSize = pageDomain.getPageSize();
        Integer pageNum = pageDomain.getPageNum();
        List<PostEsDTO> list = postService.recommend(id, pageNum, pageSize);
        return getDataTable(list);
    }
}
