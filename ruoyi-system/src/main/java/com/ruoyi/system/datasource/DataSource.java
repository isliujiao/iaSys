package com.ruoyi.system.datasource;

import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 数据源接口（新接入的数据源必须实现）
 */
public interface DataSource<T> {

    /**
     * 搜索
     *
     * @param searchText 搜索关键字
     */
//    Page<T> doSearch(String searchText, long pageNum, long pageSize);

    /**
     * 聚合搜索 - 分页查询
     *
     * @param searchText 搜索关键字
     */
    TableDataInfo doSearch(String searchText);
}
