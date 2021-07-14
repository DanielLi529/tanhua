package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Settings;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.Visitor;
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

    /**
    * @Desc: 动态评论添加记录
    * @Param: [comment]
    * @return: java.lang.Long
    */
    Long save(Comment comment);

    /**
    * @Desc: 动态评论取消评论
    * @Param: [comment]
    * @return: java.lang.Long
    */
    Long Remove(Comment comment);

    /**
     * @Desc: 获取当前动态的详细信息
     * @Param: [movementId]
     * @return: com.tanhua.domain.mongo.Publish
     */
    Publish queryPublishById(String publishId);

    /**
    * @Desc: 获取最近的五条数据
    * @Param: [userId]
    * @return: java.util.List<com.tanhua.domain.mongo.Visitor>
    */
    List<Visitor> queryVisitors(Long userId);

    /**
    * @Desc: 获取上次登录至今的五条数据
    * @Param: [userId, lastTime]
    * @return: java.util.List<com.tanhua.domain.mongo.Visitor>
    */
    List<Visitor> queryLastVisitors(Long userId, String lastTime);


    /**
     * 保存访客记录
     */
    void save(Visitor visitor);
}
