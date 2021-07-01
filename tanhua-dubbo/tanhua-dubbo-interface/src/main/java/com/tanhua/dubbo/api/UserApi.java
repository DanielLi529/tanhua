package com.tanhua.dubbo.api;

import com.tanhua.domain.db.User;

public interface UserApi {

    /**
     * 添加用户
     * @param user
     * @return
     */
    Long save(User user);

    /**
     * 通过手机号码查询
     * @param mobile
     * @return
     */
    User findByMobile(String mobile);

    /**
     * @Desc: 更新手机号码
     * @Param: []
     * @return: org.springframework.http.ResponseEntity
     */
    void updateUserInfo(User user, Long userId);

    /**
    * @Desc: 通过id获取用户对象
    * @Param: [userId]
    * @return: java.lang.String
    */
    User findUserById(Long userId);
}
