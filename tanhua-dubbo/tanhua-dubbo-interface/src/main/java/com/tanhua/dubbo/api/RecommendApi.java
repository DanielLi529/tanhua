package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Settings;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;


public interface RecommendApi {
    /**
     * 首页推荐
     * @param userId
     */
    PageResult<RecommendUser> findRecommendation(Integer page, Integer pagesize, Long userId);
}
