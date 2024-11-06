package com.ruoyi.system.mapper;

import com.ruoyi.common.core.domain.entity.Chart;
import com.ruoyi.system.domain.dto.chart.ChartQueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【chart(图表信息表)】的数据库操作Mapper
 * @createDate 2023-11-02 09:33:57
 * @Entity generator.domain.Chart
 */
@Mapper
public interface ChartMapper {

    /**
     * 新增图表信息
     */
    boolean insertChart(Chart chart);

    /**
     * 根据图表 id 修改图标的状态等信息
     */
    boolean updateChartById(Chart updateChart);

    /**
     * 根据图表 id 查询图表信息
     */
    Chart selectById(long chartId);

    /**
     * 获取当前用户生成的图表
     */
    List<Chart> listMyChartByPage(ChartQueryRequest chartQueryRequest);

    void deleteById(Long chartId);
}




