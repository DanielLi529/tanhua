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

}