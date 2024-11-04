package com.ruoyi.web.controller.search;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.system.domain.dto.post.PostEsDTO;
import com.ruoyi.system.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liujiao
 * @date 2024/11/4 16:06
 */
@RestController
@RequestMapping("/search")
public class EsPostController extends BaseController {

    @Autowired
    private PostService postService;

    /**
     * ES导入数据库所有文章
     *
     * @return
     */
    @PostMapping("/importAllList")
    public AjaxResult importAllList() {
        return success(postService.importAllList());
    }

    /**
     * 根据id批量删除商品
     */
    @PostMapping("/deleteBatchByIds")
    public AjaxResult deleteBatchByIds(@RequestParam("ids") List<Long> ids) {
        postService.deleteBatchByIds(ids);
        return success();
    }

    @PostMapping("/createBatchByIds")
    @ResponseBody
    public AjaxResult createBatchByIds(@RequestParam("ids") List<Long> ids) {
        List<PostEsDTO> esProduct = postService.createBatchByIds(ids);
        return success(esProduct);
    }

    /**
     * 查询排名前5的文章
     */
    @GetMapping("/hot")
    public AjaxResult searchHot() {
        return success(postService.searchTopFive());
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
