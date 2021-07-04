package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Announcement;
import com.tanhua.domain.vo.PageResult;


public interface AnnoApi {
    /**
     * 展示公告
     * @param
     */
   PageResult<Announcement> getAnnoList(int page, int pagesize);
}
