package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Settings;
import com.tanhua.domain.vo.PageResult;


public interface MessageApi {

    /**
    * @Desc: 添加好友
    * @Param: [userId, friendUserId]
    * @return: void
    */
    void addFriends(Long userId, Long friendUserId);

    /**
    * @Desc: 获取好友列表
    * @Param: [page, pagesize, keyword, userId]
    * @return: com.tanhua.domain.vo.PageResult
    */
    PageResult queryFriends(Integer page, Integer pagesize, String keyword, Long userId);
}
