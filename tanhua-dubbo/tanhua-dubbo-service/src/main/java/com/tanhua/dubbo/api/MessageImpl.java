package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.mapper.UserSettingMapper;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;

@Service
public class MessageImpl implements MessageApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
    * @Desc: 添加好友
    * @Param: [userId, friendUserId]
    * @return: void
    */
    @Override
    public void addFriends(Long userId, Long friendUserId) {
        // 查询是否存在数据
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId).
                and("friendId").is(friendUserId));

        if (!mongoTemplate.exists(query, Friend.class)){
            // 不存在则插入这条数据
            Friend friend = new Friend();
            friend.setId(ObjectId.get());
            friend.setUserId(userId);
            friend.setFriendId(friendUserId);
            friend.setCreated(System.currentTimeMillis());

            mongoTemplate.save(friend);
        }

        // 查询是否存在数据(反向操作)
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("friendId").is(userId).
                and("userId").is(friendUserId));

        if (!mongoTemplate.exists(query1, Friend.class)){
            // 不存在则插入这条数据
            Friend friend = new Friend();
            friend.setId(ObjectId.get());
            friend.setUserId(friendUserId);
            friend.setFriendId(userId);
            friend.setCreated(System.currentTimeMillis());

            mongoTemplate.save(friend);
        }
    }

    /**
    * @Desc: 获取好友列表
    * @Param: [page, pagesize, keyword, userId]
    * @return: com.tanhua.domain.vo.PageResult
    */
    @Override
    public PageResult queryFriends(Integer page, Integer pagesize, String keyword, Long userId) {
        // 定义查询条件
        Query query = new Query();
        // 获取总行数
        long count = mongoTemplate.count(query, Friend.class);

        query.addCriteria(Criteria.where("userId").is(userId));
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 获取符合条件的好友记录
        List<Friend> friends = mongoTemplate.find(query, Friend.class);

        // 获取总页数,封装 pageResult 对象
        long pages = count / pagesize + count % pagesize > 0 ? 1 : 0;
        PageResult pageResult = new PageResult(count, (long) pagesize, pages, (long) page, friends);

        return pageResult;
    }

    /**
    * @Desc: 喜欢列表
    * @Param: [page, pagesize, userId]
    * @return: com.tanhua.domain.vo.PageResult
    */
    @Override
    public PageResult queryLoveList(Integer page, Integer pagesize, Long userId, Integer commentType) {
        // 定义查询条件
        Query query = new Query();

        query.addCriteria(Criteria.where("publishUserId").is(userId)
                .and("commentType").is(commentType));
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 获取总行数
        long count = mongoTemplate.count(query, Comment.class);

        // 获取符合条件的好友记录
        List<Comment> friends = mongoTemplate.find(query, Comment.class);

        // 获取总页数,封装 pageResult 对象
        long pages = count / pagesize + count % pagesize > 0 ? 1 : 0;
        PageResult pageResult = new PageResult(count, (long) pagesize, pages, (long) page, friends);

        return pageResult;
    }
}
