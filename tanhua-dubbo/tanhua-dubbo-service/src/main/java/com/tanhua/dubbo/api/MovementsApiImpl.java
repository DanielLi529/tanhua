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

import java.util.ArrayList;
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

    /**
    * @Desc: 展示好友动态
    * @Param: [page, pagesize, userId]
    * @return: com.tanhua.domain.vo.PageResult
    */
    @Override
    public PageResult queryFriendPublishList(Integer page, Integer pagesize, Long userId) {
        // 通过当前用户的时间线表就能获取到所有好友的动态
        // 定义查询条件
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 获取十条时间线数据
        List<TimeLine> timeLines = mongoTemplate.find(query, TimeLine.class, "quanzi_time_line_" + userId);

        // 获取好友动态的总条数
        long counts = mongoTemplate.count(query, TimeLine.class, "quanzi_time_line_" + userId);

        // 创建集合  用来存储查询到的动态数据
        List<Publish> publishes = new ArrayList<>();
        for (TimeLine timeLine : timeLines) {
            // 通过发布id取出动态的详细信息
            Publish publish = mongoTemplate.findById(timeLine.getPublishId(), Publish.class);
            publishes.add(publish);
        }

        // 获取总页数
        long pages = counts/pagesize + counts%pagesize > 0 ? 1:0;

        // 封装 pageResult 对象
        PageResult pageResult = new PageResult(counts,(long)pagesize,pages,(long)page,publishes);
        return pageResult;
    }

    /**
    * @Desc: 展示推荐动态
    * @Param: [page, pagesize, userId]
    * @return: com.tanhua.domain.vo.PageResult
    */
    @Override
    public PageResult queryRecommendPublishList(Integer page, Integer pagesize, Long userId) {
        // 定义查询条件
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 获取十条推荐动态数据
        List<RecommendQuanzi> recommendQuanzis = mongoTemplate.find(query, RecommendQuanzi.class);

        // 获取好友动态的总条数
        long counts = mongoTemplate.count(query, RecommendQuanzi.class);

        // 创建集合  用来存储查询到的动态数据
        List<Publish> publishes = new ArrayList<>();
        for (RecommendQuanzi recommendQuanzi : recommendQuanzis) {
            if (recommendQuanzi.getPublishId() != null){
                // 通过发布id取出动态的详细信息
                Publish publish = mongoTemplate.findById(recommendQuanzi.getPublishId(), Publish.class);
                publishes.add(publish);
            }
        }

        // 获取总页数
        long pages = counts/pagesize + counts%pagesize > 0 ? 1:0;

        // 封装 pageResult 对象
        PageResult pageResult = new PageResult(counts,(long)pagesize,pages,(long)page,publishes);
        return pageResult;
    }

    /**
    * @Desc: 我的动态
    * @Param: [page, pagesize, userId]
    * @return: com.tanhua.domain.vo.PageResult
    */
    @Override
    public PageResult queryMyPublishList(Integer page, Integer pagesize, Long userId) {
        // 定义查询条件
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 获取十条推荐动态数据
        List<Album> albums = mongoTemplate.find(query, Album.class, "quanzi_album_" + userId);

        // 获取好友动态的总条数
        long counts = mongoTemplate.count(query, RecommendQuanzi.class);

        // 创建集合  用来存储查询到的动态数据
        List<Publish> publishes = new ArrayList<>();
        for (Album album : albums) {
            if (album.getPublishId() != null){
                // 通过发布id取出动态的详细信息
                Publish publish = mongoTemplate.findById(album.getPublishId(), Publish.class);
                publishes.add(publish);
            }
        }

        // 获取总页数
        long pages = counts/pagesize + counts%pagesize > 0 ? 1:0;

        // 封装 pageResult 对象
        PageResult pageResult = new PageResult(counts,(long)pagesize,pages,(long)page,publishes);
        return pageResult;
    }
}