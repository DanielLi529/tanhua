package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.mapper.UserSettingMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class RecommendApiImpl implements RecommendApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
    * @Desc: 首页推荐
    * @Param: [page, pagesize, userId]
    * @return: com.tanhua.domain.vo.PageResult<com.tanhua.domain.mongo.RecommendUser>
    */
    @Override
    public PageResult<RecommendUser> findRecommendation(Integer page, Integer pagesize, Long userId) {

        Query query = new Query();

        // 定义查询条件
        query.addCriteria(Criteria.where("toUserId").is(userId));
        query.with(Sort.by(Sort.Direction.DESC,"score"));
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 获取总记录数
        long counts = mongoTemplate.count(query, RecommendUser.class);

        // 获取分页数据
        List<RecommendUser> recommendUsers = mongoTemplate.find(query, RecommendUser.class);

        // 获取总页数
         long pages = counts/pagesize + counts%pagesize > 0 ? 1:0;

        // 封装 pageResult 并返回
        return new PageResult<>(counts,(long)pagesize,pages,(long)page,recommendUsers);
    }
}
