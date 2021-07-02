package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.RecommendUser;


public interface TodayBestApi {
    /**
     * 寻找每日佳人
     * @param userId
     */
    RecommendUser findTodayBest(Long userId);
}
