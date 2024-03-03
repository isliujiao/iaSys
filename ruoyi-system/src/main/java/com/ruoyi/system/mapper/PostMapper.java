package com.ruoyi.system.mapper;

import com.ruoyi.common.core.domain.entity.Post;
import com.ruoyi.system.domain.vo.SearchVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

/**
 * 帖子数据库操作
 */
@Mapper
public interface PostMapper {

    /**
     * 查询帖子列表（包括已被删除的数据）
     */
    List<Post> listPostWithDelete(Date minUpdateTime);

    boolean insertPostList(List<Post> postList);

    /**
     * 返回ID列表的数据
     */
    List<Post> selectByIds(List<Long> postIdList);

    /**
     * 返回电子数前5的文章
     */
    List<SearchVO> selectTopFive();
}




