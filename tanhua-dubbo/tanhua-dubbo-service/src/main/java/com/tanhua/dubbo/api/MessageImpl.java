package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.dubbo.mapper.UserSettingMapper;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;

@Service
public class MessageImpl implements MessageApi {

    @Autowired
    private MongoTemplate mongoTemplate;

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
}
