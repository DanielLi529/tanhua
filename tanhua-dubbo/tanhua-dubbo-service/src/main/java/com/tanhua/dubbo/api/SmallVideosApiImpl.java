package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.*;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

@Service
public class SmallVideosApiImpl implements SmallVideosApi {

    @Autowired
    private MongoTemplate mongoTemplate;



    /**
    * @Desc: 定义查询条件
    * @Param: [page, pagesize]
    * @return: org.springframework.data.mongodb.core.query.Query
    */
    @Override
    public void saveSmallVideo(Video video) {
        // 将数据存储到数据库中
        video.setId(ObjectId.get());
        video.setCreated(System.currentTimeMillis());
        mongoTemplate.save(video);
    }

    /**
    * @Desc: 分页查询小视频
    * @Param: [page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult
    */
    @Override
    public PageResult querySmallVideoList(Integer page, Integer  pagesize, Long userId) {
        // 定义查询条件
        Query query = new Query();

        // 获取总行数
        long count = mongoTemplate.count(query, Video.class);

        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 获取当前动态的评论对象
        List<Video> videos = mongoTemplate.find(query, Video.class);

        // 获取总页数,封装 pageResult 对象
        long pages = count / pagesize + count % pagesize > 0 ? 1 : 0;
        PageResult pageResult = new PageResult(count, (long) pagesize, pages, (long) page, videos);

        return pageResult;
    }

}