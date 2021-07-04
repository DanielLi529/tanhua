package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.mongo.Album;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.TimeLine;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.dubbo.mapper.UserSettingMapper;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class MovementsApiImpl implements MovementsApi {

    @Autowired
    private MongoTemplate mongoTemplate;


   /**
   * @Desc: 发布动态
   * @Param: [publishVo]
   * @return: void
   */
    @Override
    public void createPublish(PublishVo publishVo) {

        // 向动态表中插入 publish 对象
        long currentTimeMillis = System.currentTimeMillis();
        Publish publish = new Publish();
        BeanUtils.copyProperties(publishVo,publish);
        publish.setId(ObjectId.get());
        publish.setPid(666L);
        publish.setLocationName(publishVo.getLocation());
        publish.setCreated(currentTimeMillis);
        publish.setSeeType(1); // 1-公开
        mongoTemplate.save(publish);

        // 2. 保存到相册
        Album album = new Album();
        album.setCreated(currentTimeMillis);
        album.setPublishId(publish.getId());
        album.setId(ObjectId.get());
        mongoTemplate.save(album,"quanzi_album_" + publish.getUserId());
        // 3. 查询好友
        Query query = Query.query(Criteria.where("userId").is(publishVo.getUserId()));
        List<Friend> friends = mongoTemplate.find(query, Friend.class);

        // 4. 保存到好友表
        friends.forEach(f->{
            TimeLine timeLine = new TimeLine();
            timeLine.setCreated(currentTimeMillis);
            timeLine.setPublishId(publish.getId());
            timeLine.setUserId(publish.getUserId());
            timeLine.setId(ObjectId.get());
            mongoTemplate.save(timeLine,"quanzi_time_line_" + f.getFriendId());
        });
    }
}
