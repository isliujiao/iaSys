package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.entity.Picture;

import java.util.List;

/**
 * 图片服务
 *
 */
public interface PictureService {

    List<Picture> searchPicture(String searchText);
}
