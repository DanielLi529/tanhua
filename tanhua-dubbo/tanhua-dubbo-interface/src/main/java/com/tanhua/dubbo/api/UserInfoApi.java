package com.tanhua.dubbo.api;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.PageResult;


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

    /**
    * @Desc: 喜欢的数量
    * @Param: [userId]
    * @return: java.lang.Long
    */
    Long queryLoveCount(Long userId);

    /**
    * @Desc: 粉丝数量
    * @Param: [userId]
    * @return: java.lang.Long
    */
    Long queryFansCount(Long userId);

    /**
    * @Desc: 相互喜欢的数量
    * @Param: [userId]
    * @return: java.lang.Long
    */
    Long queryEachLoveCount(Long userId);

    /**
    * @Desc: 查询我喜欢的
    * @Param: [userId, page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult
    */
    PageResult findPageLikeEachOther(Long userId, int page, int pagesize);

    /**
    * @Desc: 查询粉丝列表
    * @Param: [userId, page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult
    */
    PageResult findPageOneSideLike(Long userId, int page, int pagesize);

    /**
    * @Desc: 相互喜欢
    * @Param: [userId, page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult
    */
    PageResult findPageFens(Long userId, int page, int pagesize);

    /**
    * @Desc: 访客列表
    * @Param: [userId, page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult
    */
    PageResult findPageMyVisitors(Long userId, int page, int pagesize);
}
