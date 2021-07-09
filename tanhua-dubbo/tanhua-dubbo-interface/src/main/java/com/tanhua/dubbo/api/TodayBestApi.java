package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;


public interface TodayBestApi {
    /**
     * 寻找每日佳人
     * @param userId
     */
    RecommendUser findTodayBest(Long userId);

    /**
    * @Desc: 查询和当前佳人是否存在缘分
    * @Param: [id, userId]
    * @return: com.tanhua.domain.mongo.RecommendUser
    */
    RecommendUser findPersonInfo(Long id, Long userId);
}
