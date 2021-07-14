package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.mongo.UserLocation;
import com.tanhua.dubbo.mapper.UserSettingMapper;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Service
public class BaiduApiImpl implements BaiduApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    // 上报地理位置
    @Override
    public void reportLocation(Double latitude, Double longitude, String addrStr, Long userId) {
        long millis = System.currentTimeMillis();
        // 查询数据库当前是否存在该用户的地理位置
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));

        boolean result = mongoTemplate.exists(query, UserLocation.class);

        if (result){
            // 更新数据
            Update update = new Update();
            update.set("updated",millis);
            update.set("lastUpdated",millis);
            update.set("location",new GeoJsonPoint(latitude, longitude));

            // 保存数据
            mongoTemplate.updateFirst(query,update,UserLocation.class);
        }else{
            // 保存数据，创建对象
            UserLocation user = new UserLocation();
            user.setId(ObjectId.get());
            user.setUserId(userId);
            user.setCreated(millis);
            user.setUpdated(millis);
            user.setLastUpdated(millis);
            user.setAddress(addrStr);
            user.setLocation(new GeoJsonPoint(latitude,longitude));
            // 保存数据
            mongoTemplate.save(user);
        }
    }
}
