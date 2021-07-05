package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Settings;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;

import java.util.List;


public interface MovementsApi {

    /**
    * @Desc: 发布动态
    * @Param: [publishVo]
    * @return: void
    */
    void createPublish(PublishVo publishVo);

    /**
    * @Desc: 展示好友动态
    * @Param: [page, pagesize, userId]
    * @return: com.tanhua.domain.vo.PageResult
    */
    PageResult queryFriendPublishList(Integer page, Integer pagesize, Long userId);

    /**
    * @Desc: 展示推荐动态
    * @Param: [page, pagesize, userId]
    * @return: com.tanhua.domain.vo.PageResult
    */
    PageResult queryRecommendPublishList(Integer page, Integer pagesize, Long userId);

    /**
    * @Desc: 我的动态
    * @Param: [page, pagesize, userId]
    * @return: com.tanhua.domain.vo.PageResult
    */
    PageResult queryMyPublishList(Integer page, Integer pagesize, Long userId);
}
