package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Announcement;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;


public interface SmallVideosApi {

    /**
     * 发布小视频
     * @param
     */
    void saveSmallVideo(Video video);
}
