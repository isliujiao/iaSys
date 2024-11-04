package com.ruoyi.web.controller.search;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.dto.search.SearchRequest;
import com.ruoyi.system.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
