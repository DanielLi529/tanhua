package com.tanhua.dubbo.api;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.mongo.UserLike;
import com.tanhua.domain.mongo.Visitor;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

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

    /**
    * @Desc: 我喜欢的
    * @Param: [userId, page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult
    */
    @Override
    public PageResult findPageLikeEachOther(Long userId, int page, int pagesize) {
        // 定义查询条件
        Query query = new Query();
        // 获取总行数
        long count = mongoTemplate.count(query, UserLike.class);

        query.addCriteria(Criteria.where("userId").is(userId));
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 获取符合条件的好友记录
        List<UserLike> userLikes = mongoTemplate.find(query, UserLike.class);

        List<RecommendUser> users = new ArrayList<>();
        for (UserLike userLike : userLikes) {
            users.add(queryScore(userLike.getLikeUserId(),userLike.getUserId()));
        }

        // 获取总页数,封装 pageResult 对象
        long pages = count / pagesize + count % pagesize > 0 ? 1 : 0;
        PageResult pageResult = new PageResult(count, (long) pagesize, pages, (long) page, users);

        return pageResult;
    }

    /**
    * @Desc: 我的粉丝
    * @Param: [userId, page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult
    */
    @Override
    public PageResult findPageOneSideLike(Long userId, int page, int pagesize) {
        // 定义查询条件
        Query query = new Query();
        // 获取总行数
        long count = mongoTemplate.count(query, UserLike.class);

        query.addCriteria(Criteria.where("likeUserId").is(userId));
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 获取符合条件的好友记录
        List<UserLike> userLikes = mongoTemplate.find(query, UserLike.class);

        List<RecommendUser> users = new ArrayList<>();
        for (UserLike userLike : userLikes) {
            users.add(queryScore(userLike.getLikeUserId(),userLike.getUserId()));
        }

        // 获取总页数,封装 pageResult 对象
        long pages = count / pagesize + count % pagesize > 0 ? 1 : 0;
        PageResult pageResult = new PageResult(count, (long) pagesize, pages, (long) page, users);

        return pageResult;
    }

    /**
    * @Desc: 互相喜欢
    * @Param: [userId, page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult
    */
    @Override
    public PageResult findPageFens(Long userId, int page, int pagesize) {
        // 定义查询条件
        Query query = new Query();
        // 获取总行数
        long count = mongoTemplate.count(query, Friend.class);

        query.addCriteria(Criteria.where("userId").is(userId));
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 获取符合条件的好友记录
        List<Friend> friends = mongoTemplate.find(query, Friend.class);

        List<RecommendUser> users = new ArrayList<>();
        for (Friend friend : friends) {
            RecommendUser user = queryScore(friend.getFriendId(),friend.getUserId());
            users.add(user);
        }

        // 获取总页数,封装 pageResult 对象
        long pages = count / pagesize + count % pagesize > 0 ? 1 : 0;
        PageResult pageResult = new PageResult(count, (long) pagesize, pages, (long) page, users);

        return pageResult;
    }

    /**
    * @Desc: 最近访客信息
    * @Param: [userId, page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult
    */
    @Override
    public PageResult findPageMyVisitors(Long userId, int page, int pagesize) {
        // 定义查询条件
        Query query = new Query();
        // 获取总行数
        long count = mongoTemplate.count(query, Visitor.class);

        query.addCriteria(Criteria.where("userId").is(userId));
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 获取符合条件的好友记录
        List<Visitor> visitors = mongoTemplate.find(query, Visitor.class);

        List<RecommendUser> users = new ArrayList<>();
        for (Visitor visitor : visitors) {
            users.add(queryScore(visitor.getVisitorUserId(),userId));
        }

        // 获取总页数,封装 pageResult 对象
        long pages = count / pagesize + count % pagesize > 0 ? 1 : 0;
        PageResult pageResult = new PageResult(count, (long) pagesize, pages, (long) page, users);

        return pageResult;
    }


    /**
    * @Desc: 封装 RecommendUser 对象
    * @Param: [userId, toUserId]
    * @return: com.tanhua.domain.mongo.RecommendUser
    */
    private RecommendUser queryScore(Long userId, Long toUserId) {
        Query query = Query.query(Criteria.where("toUserId").is(toUserId).and("userId").is(userId));
        RecommendUser user = this.mongoTemplate.findOne(query, RecommendUser.class);
        if (user == null) {
            user = new RecommendUser();
            user.setUserId(userId);
            user.setToUserId(toUserId);
            user.setScore(95d);
        }
        return user;
    }

}
