package com.ruoyi.web.controller.bi;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.ChatToAIUtils;
import com.ruoyi.system.domain.dto.chart.ChartQueryRequest;
import com.ruoyi.system.domain.dto.chart.GenChartByAiRequest;
import com.ruoyi.system.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

/**
 * 图表
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController extends BaseController {

    @Autowired
    private ChartService chartService;

    /**
     * 分页查询当前用户生成的图表数据
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/my/list/page")
    public TableDataInfo listMyChartByPage(ChartQueryRequest chartQueryRequest,
                                           HttpServletRequest request) {
        startPage();
        return getDataTable(chartService.listMyChartByPage(chartQueryRequest,request));
    }

    /**
     * 根据图标id删除图表数据
     *
     * @return
     */
    @GetMapping("/my/list/deleteById/{chartId}")
    public AjaxResult deleteById(@PathVariable Long chartId) {
        chartService.deleteById(chartId);
        return success();
    }


    /**
     * 使用AI生产图表数据
     */
    @PostMapping("/gen")
    public AjaxResult genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                   GenChartByAiRequest genChartByAiRequest) {
        return success(chartService.genChartByAi(multipartFile, genChartByAiRequest));
    }

    /**
     * 智能分析（异步）
     * 弃用
     */
//    @Deprecated
//    @PostMapping("/gen/async")
//    public AjaxResult genChartByAiAsync(@RequestPart("file") MultipartFile multipartFile,
//                                        GenChartByAiRequest genChartByAiRequest) {
//        return success(chartService.genChartByAiAsync(multipartFile, genChartByAiRequest));
//    }


    /**
     * 智能分析（消息队列MQ-异步）
     */
    @PostMapping("/gen/async/mq")
    public AjaxResult genChartByAiAsyncMq(@RequestPart("file") MultipartFile multipartFile,
                                          GenChartByAiRequest genChartByAiRequest) {
        return success(chartService.genChartByAiAsyncMq(multipartFile, genChartByAiRequest));
    }


    // region 增删改查


    @GetMapping("/test")
    public AjaxResult test(@RequestParam("msg") String msg) {
        log.info("msg = {}", ChatToAIUtils.askTyqwEcharts(msg));
        return success();
    }
}
