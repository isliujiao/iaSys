package com.ruoyi.common.enums;

/**
 * 图标类型状态信息
 *
 * @Author
 * @Date 2023/11/8 11:18
 * @Description
 */
public enum ChartStatusEnum {

    SUCCEED("succeed","成功"),

    FAILED("failed","失败"),

    RUNNING("running","进行中");

    /**
     * 状态码
     */
    private String statusCode;
    /**
     * 状态信息
     */
    private String statusMsg;

    ChartStatusEnum(String statusCode, String statusMsg) {
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }
}
