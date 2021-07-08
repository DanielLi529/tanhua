package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Announcement;
import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;

import java.util.List;


public interface SmallVideosApi {

    /**
     * 发布小视频
     * @param
     */
    void saveSmallVideo(Video video);

    /**
    * @Desc: 分页查询小视频
    * @Param: [page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult
    */
    PageResult querySmallVideoList(Integer page, Integer  pagesize, Long userId);

    /**
    * @Desc: 查询关注表内有没有这条数据
    * @Param: [userId, toUserId]
    * @return: com.tanhua.domain.mongo.FollowUser
    */
    List<FollowUser> queryFollow(Long userId, Long toUserId);

    /**
    * @Desc: 向关注表中插入数据
    * @Param: [userId, toUserId]
    * @return: void
    */
    void saveFollowUser(FollowUser followUser);

    /**
    * @Desc: 取消关注，删除数据
    * @Param: [userId, toUserId]
    * @return: void
    */
    void removeFollowUser(Long userId, Long toUserId);
}
