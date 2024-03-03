package com.ruoyi.system.datasource;

import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.stereotype.Component;

/**
 * 视频数据源
 *
 */
@Component
public class VideoDataSource implements DataSource<Object> {

    @Override
    public TableDataInfo doSearch(String searchText) {
        return null;
    }

}
