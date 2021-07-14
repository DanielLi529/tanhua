package com.tanhua.dubbo.api;

import com.mongodb.client.MongoCollection;
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
public class MovementsApiImpl implements MovementsApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
    * @Desc: 定义查询条件
    * @Param: [page, pagesize]
    * @return: org.springframework.data.mongodb.core.query.Query
    */
    private Query getQueryConditions(Integer page, Integer pagesize) {
        // 定义查询条件
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page - 1) * pagesize);
        return query;
    }

    /**
    * @Desc: 封装 pageResult
    * @Param: [page, pagesize, counts, publishes]
    * @return: com.tanhua.domain.vo.PageResult
    */
    private PageResult getPageResult(long page, Integer pagesize, long counts, List<Publish> publishes) {
        // 获取总页数
        long pages = counts / pagesize + counts % pagesize > 0 ? 1 : 0;

        // 封装 pageResult 对象
        return new PageResult(counts, (long) pagesize, pages, page, publishes);
    }

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
        Query query = getQueryConditions(page, pagesize);

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
        // 封装 返回的数据
        PageResult pageResult = getPageResult(page, pagesize, counts, publishes);
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
        Query query = getQueryConditions(page, pagesize);

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
        // 封装 返回的数据
        PageResult pageResult = getPageResult(page, pagesize, counts, publishes);
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
        Query query = getQueryConditions(page, pagesize);

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
        // 封装 返回的数据
        PageResult pageResult = getPageResult(page, pagesize, counts, publishes);
        return pageResult;
    }

    /**
    * @Desc: 动态点赞
    * @Param: [comment]
    * @return: java.lang.Long
    */
    @Override
    public Long save(Comment comment) {
        // 添加 comment
        mongoTemplate.save(comment);

        // 更新当前动态的喜欢数量
        updateCount(comment,+1);

        // 获取动态更新后的喜欢数量
        long count = getCount(comment);

        return count;
    }

    /**
    * @Desc: 动态取消点赞
    * @Param: [comment]
    * @return: java.lang.Long
    */
    @Override
    public Long Remove(Comment comment) {
        // 删除 comment 的记录
        Query query = new Query();
        query.addCriteria(Criteria.where("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType())
                .and("userId").is(comment.getUserId())
        );

        mongoTemplate.remove(query,Comment.class);

        // 更新当前动态的喜欢数量
        updateCount(comment,-1);

        // 获取动态更新后的喜欢数量
        long count = getCount(comment);
        return count;
    }

    /**
    * @Desc: 更新动态表中对应的计数值
    * @Param: [comment, value]
    * @return: void
    */
    private void updateCount(Comment comment,int value){
        Query updateQuery = new Query();
        updateQuery.addCriteria(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        update.inc(comment.getCol(),value);
        mongoTemplate.updateFirst(updateQuery,update,Publish.class);
    }

    /**
    * @Desc: 获取当前动态的评论数量
    * @Param: [comment]
    * @return: long
    */
    private long getCount(Comment comment){
        Query query = new Query(Criteria.where("id").is(comment.getPublishId()));
        if(comment.getPubType() == 1){
            Publish publish = mongoTemplate.findOne(query, Publish.class);
            if(comment.getCommentType() == 1){// //评论类型，1-点赞，2-评论，3-喜欢
                return (long)publish.getLikeCount();
            }
            if(comment.getCommentType() == 2){// //评论类型，1-点赞，2-评论，3-喜欢
                return (long)publish.getCommentCount();
            }
            if(comment.getCommentType() == 3){// //评论类型，1-点赞，2-评论，3-喜欢
                return (long)publish.getLoveCount();
            }
        }
        if(comment.getPubType() == 3){
            Comment cm = mongoTemplate.findOne(query, Comment.class);
            return (long)cm.getLikeCount();
        }
        return 99l;
    }

    /**
     * @Desc: 获取当前动态的详细信息
     * @Param: [movementId]
     * @return: com.tanhua.domain.mongo.Publish
     */
    @Override
    public Publish queryPublishById(String publishId) {
        return mongoTemplate.findById(new ObjectId(publishId), Publish.class);
    }

    /**
    * @Desc: 谁看过我 最近访客 5条记录
    * @Param: [userId]
    * @return: java.util.List<com.tanhua.domain.mongo.Visitor>
    */
    @Override
    public List<Visitor> queryVisitors(Long userId) {
        Query query = new Query();

        // 定义查询条件
        query.addCriteria(Criteria.where("userId").is(userId));
        query.with(Sort.by(Sort.Direction.DESC,"data"));
        query.limit(5);

        // 返回数据
        return mongoTemplate.find(query, Visitor.class);
    }

    /**
     * @Desc: 谁看过我 上次登录时间之后的访客 5条记录
     * @Param: [userId]
     * @return: java.util.List<com.tanhua.domain.mongo.Visitor>
     */
    @Override
    public List<Visitor> queryLastVisitors(Long userId, String lastTime) {
        Query query = new Query();

        // 定义查询条件
        query.addCriteria(Criteria.where("userId").is(userId).and("date").gt(lastTime));
        query.with(Sort.by(Sort.Direction.DESC,"data"));
        query.limit(5);

        // 返回数据
        return mongoTemplate.find(query, Visitor.class);
    }

    /**
     * 保存访客记录
     */
    @Override
    public void save(Visitor visitor) {
        visitor.setId(ObjectId.get());
        visitor.setDate(System.currentTimeMillis());
        mongoTemplate.save(visitor);
    }
}