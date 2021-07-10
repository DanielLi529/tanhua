package com.tanhua.dubbo.api;

import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.PageResult;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class CommentsApiImpl implements CommentsApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @Desc: 展示动态的所有评论信息
     * @Param: [page, pagesize, userId]
     * @return: com.tanhua.domain.vo.PageResult
     */
    @Override
    public PageResult queryCommentList(Integer page, Integer pagesize, String movementId) {

        // 定义查询条件
        Query query = new Query();
        query.addCriteria(Criteria.where("publishId").is(new ObjectId(movementId))
                .and("commentType").is(2));

        // 获取总行数
        long count = mongoTemplate.count(query, Comment.class);

        query.with(Sort.by(Sort.Direction.DESC, "created"));
        query.limit(pagesize).skip((page - 1) * pagesize);

        // 获取当前动态的评论对象
        List<Comment> comments = mongoTemplate.find(query, Comment.class);

        // 获取总页数,封装 pageResult 对象
        long pages = count / pagesize + count % pagesize > 0 ? 1 : 0;
        PageResult pageResult = new PageResult(count, (long) pagesize, pages, (long) page, comments);

        return pageResult;
    }

    /**
    * @Desc: 根据发布Id在发布表中查询用户的UserId
    * @Param: [page, pagesize, movementId]
    * @return: com.tanhua.domain.vo.PageResult
    */
    @Override
    public Long queryUserIdByPublishId(String publishId) {

        // 定义查询条件
        Publish publish = mongoTemplate.findById(publishId, Publish.class);

        return publish.getUserId();
    }
}