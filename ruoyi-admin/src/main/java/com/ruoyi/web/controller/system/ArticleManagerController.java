package com.ruoyi.web.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.Post;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.ArticleManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章管理
 */
@RestController
@RequestMapping("/articleManager")
public class ArticleManagerController extends BaseController {

    @Autowired
    private ArticleManagerService articleManagerService;


    // 存储用户发布的文章
    // @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "存储用户发布的文章", businessType = BusinessType.INSERT)
    @PostMapping("/insertPostList")
    public AjaxResult insertPostList(@Validated @RequestBody Post post) {
        return toAjax(articleManagerService.insertPostList(post));
    }

    /**
     * 批量处理List<Map>中的所有BigDecimal字段
     * 将科学计数法表示的0转换为标准的0
     */
    public static void formatBigDecimals(List<Map<String, Object>> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }

        // 遍历每条数据
        for (Map<String, Object> dataMap : dataList) {
            // 遍历Map中的每个字段
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                Object value = entry.getValue();
                // 只处理BigDecimal类型
                if (value instanceof BigDecimal) {
                    BigDecimal bd = (BigDecimal) value;
                    // 处理科学计数法表示的0（如0E-8）
                    if (isZero(bd)) {
                        entry.setValue(BigDecimal.ZERO);
                    } else {
                        // 可选：对非零值也进行格
                        // 式化，去除不必要的小数位
                        entry.setValue(bd.stripTrailingZeros());
                    }
                }
            }
        }
    }

    /**
     * 判断BigDecimal是否为0（考虑科学计数法情况）
     */
    private static boolean isZero(BigDecimal bd) {
        // 去除末尾的0后比较是否等于0
        return bd.stripTrailingZeros().compareTo(BigDecimal.ZERO) == 0;
    }

    public static void main(String[] args) {
        ArrayList<Map<String,Object>> objects = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("a",new BigDecimal("0E-8"));
        objects.add(map);
        // formatBigDecimals(objects);
        System.out.println(objects);
    }
}
