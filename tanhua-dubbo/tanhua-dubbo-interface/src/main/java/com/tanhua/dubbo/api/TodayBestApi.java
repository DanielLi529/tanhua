package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.UserLocationVo;

import java.util.List;


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

    /**
    * @Desc: 搜附近的人
    * @Param: [userId, valueOf]
    * @return: java.util.List<com.tanhua.domain.vo.UserLocationVo>
    */
    List<UserLocationVo> searchNear(Long userId, Long valueOf);

}
