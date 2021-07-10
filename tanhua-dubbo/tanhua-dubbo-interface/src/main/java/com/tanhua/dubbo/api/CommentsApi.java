package com.tanhua.dubbo.api;


import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.PageResult;

public interface CommentsApi {

    /**
    * @Desc: 获取当前动态的所有评论
    * @Param: [page, pagesize, movementId]
    * @return: com.tanhua.domain.vo.PageResult
    */
    PageResult queryCommentList(Integer page, Integer pagesize, String movementId);

    /**
    * @Desc: 获取当前动态的发布人 Id
    * @Param: [publishId]
    * @return: java.lang.Long
    */
    Long queryUserIdByPublishId(String publishId);
}
