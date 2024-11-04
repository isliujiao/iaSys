package com.ruoyi.system.repository;

import com.ruoyi.system.domain.dto.post.PostEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 帖子 ES 操作
 */
public interface PostEsRepository extends ElasticsearchRepository<PostEsDTO, Long> {
	/**
     * 创建自定义方法，根据ID查找
     */
    List<PostEsDTO> findByUserId(Long userId);
}
