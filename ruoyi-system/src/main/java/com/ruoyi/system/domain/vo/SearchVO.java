package com.ruoyi.system.domain.vo;

import com.ruoyi.common.core.domain.entity.Picture;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 聚合搜索
 */
@Data
public class SearchVO implements Serializable {

    private TableDataInfo sysUser;

    private TableDataInfo postList;

    private TableDataInfo pictureList;

    private TableDataInfo dataList;

    private static final long serialVersionUID = 1L;

}
