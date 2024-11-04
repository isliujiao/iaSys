package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.entity.Post;
import com.ruoyi.system.domain.dto.post.PostEsDTO;
import com.ruoyi.system.domain.dto.post.PostQueryRequest;
import com.ruoyi.system.domain.vo.SearchVO;

import java.util.List;

/**
 * 帖子服务
 */
public interface PostService {


    /**
     * 从 ES 查询
     *
     * @param postQueryRequest
     * @return
     */
    List<Post> searchFromEs(PostQueryRequest postQueryRequest);

    /**
     * 获取帖子信息插入数据库
     *
     * @param postList
     * @return
     */
    boolean insertPostList(List<Post> postList);

    /**
     * 获取点赞前5的文章
     *
     * @return
     */
    List<SearchVO> searchTopFive();

    /**
     * 根据文章ID推荐相关文章
     *
     * @param id       文章ID
     * @param pageNum  当前页
     * @param pageSize 每页大小
     * @return
     */
    List<PostEsDTO> recommend(Long id, Integer pageNum, Integer pageSize);

    /**
     * ES导入数据库所有文章
     *
     * @return
     */
    int importAllList();

    /**
     * 批量删除
     */
    void deleteBatchByIds(List<Long> ids);

    /**
     * 创建ES索引
     *
     * @param ids
     * @return
     */
    List<PostEsDTO> createBatchByIds(List<Long> ids);

    /**
     * 校验
     *
     * @param post
     * @param add
     */
//    void validPost(Post post, boolean add);

    /**
     * 获取查询条件
     *
     * @param postQueryRequest
     * @return
     */
//    QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param post
     * @param request
     * @return
     */
//    PostVO getPostVO(Post post, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param postPage
     * @param request
     * @return
     */
//    Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request);


    /**
     * 分页查询帖子
     * @param postQueryRequest
     * @param request
     * @return
     */
//    Page<PostVO> listPostVoByPage(PostQueryRequest postQueryRequest, HttpServletRequest request);

}
