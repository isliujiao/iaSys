package com.ruoyi.system.domain.vo;

import lombok.Data;

/**
 * BI 返回结果
 *
 * @author liujiao
 * @date 2023/12/16 20:33
 */
@Data
public class BiResponseVO {

    /**
     * 返回图表数据
     */
    private String genChart;

    /**
     * 返回分析结果
     */
    private String genResult;

    /**
     * 新生成的图标ID
     */
    private Long chartId;

}
