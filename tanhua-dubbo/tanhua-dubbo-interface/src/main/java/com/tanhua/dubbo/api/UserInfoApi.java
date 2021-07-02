package com.tanhua.dubbo.api;

import com.tanhua.domain.db.UserInfo;


public interface UserInfoApi {
    /**
     * 保存用户基础信息
     * @param userInfo
     */
    void saveUserInfo(UserInfo userInfo);

    /**
     * 通过id更新用户信息
     * @param userInfo
     */
    void updateUserInfo(UserInfo userInfo);

    /**
    * @Desc: 通过id获取用户信息
    * @Param: [id]
    * @return: com.tanhua.domain.db.UserInfo
    */
    UserInfo getUserInfo(Long id);

    /**
    * @Desc: 更新用户头像
    * @Param: [userInfo]
    * @return: void
    */
    void updateUserHead(UserInfo userInfo);
}
