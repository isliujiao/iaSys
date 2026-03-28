package com.ruoyi.web.controller;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.epoint.EbpuSaaS.EbpuSaaSClient;
import com.epoint.EbpuSaaS.EbpuSaaSObject;
import com.epoint.EbpuSaaS.SaaSEnum.RAGEnum;
import com.epoint.EbpuSaaS.model.AIAssistantRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class Test {

    public static void main(String[] args) {
        System.out.println(setChargeTimeFromDateStr("20250821"));
    }

    public static Timestamp setChargeTimeFromDateStr(String dateStr) {
        // 1. 空值防护
        if (dateStr == null || dateStr.trim().length() != 8) {
            return new Timestamp(System.currentTimeMillis());
        }

        // 2. 定义格式化规则（指定GMT+8时区，避免时区偏移）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8")); // 强制指定时区

        try {
            // 3. 补全时间部分（默认00:00:00）并解析为Date
            String fullDateStr = dateStr + " 00:00:00";
            Date date = sdf.parse(fullDateStr);

            // 4. 转换为Timestamp并赋值
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            return new Timestamp(System.currentTimeMillis());
        }
    }
}
