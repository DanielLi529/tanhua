package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.mongo.UserLocation;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.UserLocationVo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class TodayBestApiImpl implements TodayBestApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 寻找每日佳人
     *
     * @param toUserId
     */
    @Override
    public RecommendUser findTodayBest(Long toUserId) {
        Criteria criteria = Criteria.where("toUserId").is(toUserId);

        Query query = new Query(criteria).with(Sort.by(Sort.Order.desc("score"))).limit(1);

        return mongoTemplate.findOne(query,RecommendUser.class);
    }

    /**
    * @Desc: 查询和当前佳人是否存在缘分
    * @Param: [id, userId]
    * @return: com.tanhua.domain.mongo.RecommendUser
    */
    @Override
    public RecommendUser findPersonInfo(Long id, Long userId) {
        // 设置查询条件
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId).
                and("toUserId").is(id));

        RecommendUser one = mongoTemplate.findOne(query, RecommendUser.class);
        return one;
    }

    /**
    * @Desc: 搜附近的人
    * @Param: [userId, valueOf]
    * @return: java.util.List<com.tanhua.domain.vo.UserLocationVo>
    */
    @Override
    public List<UserLocationVo> searchNear(Long userId, Long miles) {
        //1、根据用户id，查询当前用户的位置
        Query query = Query.query(Criteria.where("userId").is(userId));
        UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);
        //2、指定查询的半径范围
        GeoJsonPoint location = userLocation.getLocation();
        Distance distance = new Distance(miles/1000, Metrics.KILOMETERS);
        //3、根据此半径画圆
        Circle circle = new Circle(location,distance); //圆点，半径
        //4、调用mongotemplate查询 List<UserLocation>
        Query nearQuery = new Query(
                Criteria.where("location").withinSphere(circle)
        );
        List<UserLocation> userLocations = mongoTemplate.find(nearQuery, UserLocation.class);
        //5、转化为List<UserLocationVo>
        return UserLocationVo.formatToList(userLocations);
    }
}
