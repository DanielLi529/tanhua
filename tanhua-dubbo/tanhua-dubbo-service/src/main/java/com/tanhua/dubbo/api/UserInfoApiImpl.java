package com.tanhua.dubbo.api;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.UserLike;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Service
public class UserInfoApiImpl implements UserInfoApi {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存用户基本信息
     * @param userInfo
     */
    @Override
    public void saveUserInfo(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    /**
     * 通过id更新用户基本信息
     * @param userInfo
     */
    @Override
    public void updateUserInfo(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    /**
     * 通过id查找用户基本信息
     * @param id
     */
    @Override
    public UserInfo getUserInfo(Long id) {
        return userInfoMapper.selectById(id);
    }

    /**
    * @Desc: 更新用户头像
    * @Param: [userInfo]
    * @return: void
    */
    @Override
    public void updateUserHead(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    /**
     * @Desc: 我喜欢的
     * @Param: [userId]
     * @return: java.lang.Long
     */
    @Override
    public Long queryFansCount(Long userId) {
        // 定义查询条件
        Query query = new Query(
                Criteria.where("userId").is(userId)
        );
        return mongoTemplate.count(query, UserLike.class);
    }

    /**
     * @Desc: 我的粉丝
     * @Param: [userId]
     * @return: java.lang.Long
     */
    @Override
    public Long queryEachLoveCount(Long userId) {
        // 定义查询条件
        Query query = new Query(
                Criteria.where("likeUserId").is(userId)
        );
        return mongoTemplate.count(query, UserLike.class);
    }

    /**
    * @Desc: 相互喜欢的
    * @Param: [userId]
    * @return: java.lang.Long
    */
    @Override
    public Long queryLoveCount(Long userId) {
        // 定义查询条件
        Query query = new Query(
                Criteria.where("userId").is(userId)
        );
        return mongoTemplate.count(query, Friend.class);
    }
}
