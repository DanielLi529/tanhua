package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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
}
