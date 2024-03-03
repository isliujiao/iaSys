package com.ruoyi.system.datasource;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.vo.UserVO;
import com.ruoyi.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
@Slf4j
public class UserDataSource extends BaseController implements DataSource<UserVO> {

//    @Resource
//    private UserService userService;

    @Autowired
    private ISysUserService userService;

    @Override
    public TableDataInfo doSearch(String searchText) {
        startPage();
        return getDataTable(userService.selectUserByKey(searchText));
    }

//    @Override
//    public Page<UserVO> doSearch(String searchText, long pageNum, long pageSize) {
//        UserQueryRequest userQueryRequest = new UserQueryRequest();
//        userQueryRequest.setUserName(searchText);
//        userQueryRequest.setCurrent(pageNum);
//        userQueryRequest.setPageSize(pageSize);
//        return userService.listUserVOByPage(userQueryRequest);
//    }
}
