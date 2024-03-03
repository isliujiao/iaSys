package com.ruoyi.web.controller.search;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.dto.search.SearchRequest;
import com.ruoyi.system.service.SearchService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/search")
public class SearchController extends BaseController {


    @Resource
    private SearchService searchService;

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

}
