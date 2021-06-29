package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Settings;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.PageResult;


public interface UserBlackListApi {

    /**
     * 展示用户通用设置
     * @param userId
     */
    PageResult<UserInfo> getBlackList(int page, int pagesize, Long userId);


    /**
    * @Desc: 移除黑名单
    * @Param: [deleteUserId, userId]
    * @return: void
    */
    void deleteBlackList(long deleteUserId, Long userId);
}
