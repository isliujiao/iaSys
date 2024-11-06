package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.entity.Chart;
import com.ruoyi.system.domain.dto.chart.ChartQueryRequest;
import com.ruoyi.system.domain.dto.chart.GenChartByAiRequest;
import com.ruoyi.system.domain.vo.BiResponseVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【chart(图表信息表)】的数据库操作Service
 * @createDate 2023-11-02 09:33:57
 */
public interface ChartService {

    /**
     * 使用 AI 生成图表数据
     * @param multipartFile       数据文件
     * @param genChartByAiRequest 生成图表信息
     * @return 是否生成成功
     */
    BiResponseVO genChartByAi(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest);

    /**
     * 使用 AI 异步 生成图表数据
     */
    Boolean genChartByAiAsync(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest);

    /**
     * 使用 AI 生成图表数据，并放入mq中
     */
    Boolean genChartByAiAsyncMq(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest);

    /**
     * 根据图表 id 查询图表信息
     */
    Chart getChartById(long chartId);

    /**
     * 根据图表 id 修改图标的状态等信息
     */
    Boolean updateChartById(Chart updateChartResult);

    /**
     * 获取当前用户生成的图表
     */
    List<Chart> listMyChartByPage(ChartQueryRequest chartQueryRequest, HttpServletRequest request);

    /**
     * 根据图表 id 删除图表信息
     * @param chartId
     */
    void deleteById(Long chartId);
}
