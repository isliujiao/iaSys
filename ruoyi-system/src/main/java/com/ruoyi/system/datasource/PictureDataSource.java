package com.ruoyi.system.datasource;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.entity.Picture;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 图片服务实现类
 */
@Service
public class PictureDataSource extends BaseController implements DataSource<Picture> {

    @Autowired
    PictureService pictureService;

    @Override
    public TableDataInfo doSearch(String searchText) {
        startPage();
        return getDataTable(pictureService.searchPicture(searchText));
    }

//    @Override
//    public Page<Picture> doSearch(String searchText, long pageNum, long pageSize) {
//        long current = (pageNum - 1) * pageSize;
//        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%s", searchText, current);
//        Document doc = null;
//        try {
//            doc = Jsoup.connect(url).get();
//        } catch (IOException e) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据获取异常");
//        }
//        Elements elements = doc.select(".iuscp.isv");
//        List<Picture> pictures = new ArrayList<>();
//        for (Element element : elements) {
//            // 取图片地址（murl）
//            String m = element.select(".iusc").get(0).attr("m");
//            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
//            String murl = (String) map.get("murl");
//            // 取标题
//            String title = element.select(".inflnk").get(0).attr("aria-label");
//
//            Picture picture = new Picture();
//            picture.setTitle(title);
//            picture.setUrl(murl);
//            pictures.add(picture);
//            if (pictures.size() >= pageSize) {
//                break;
//            }
//        }
//        Page<Picture> picturePage = new Page<>(pageNum, pageSize);
//        picturePage.setRecords(pictures);
//        return picturePage;
//    }

}
