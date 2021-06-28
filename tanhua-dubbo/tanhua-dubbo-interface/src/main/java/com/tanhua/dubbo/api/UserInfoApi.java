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

    UserInfo getUserInfo(Long id);

    void updateUserHead(UserInfo userInfo);
}
