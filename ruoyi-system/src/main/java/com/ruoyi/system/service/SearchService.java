package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.search.SearchRequest;
import com.ruoyi.system.domain.vo.SearchVO;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author
 * @Date 2023/11/17 10:09
 * @Description
 */
public interface SearchService {

    SearchVO searchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request);

}
