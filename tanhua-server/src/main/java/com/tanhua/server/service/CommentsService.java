package com.tanhua.server.service;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.MomentVo;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.CommentVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.CommentsApi;
import com.tanhua.dubbo.api.MovementsApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.RelativeDateFormat;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户管理业务层
 */
@Service
public class CommentsService {

    @Reference
    private CommentsApi commentsApi;

    @Reference
    private UserInfoApi userInfoApi;

    @Reference
    private MovementsApi movementsApi;

    /**
     * @Desc: 获取当前动态的评论
     * @Param: [content]
     * @return: void
     */
    public PageResult queryCommentList(Integer page, Integer pagesize, String movementId) {
        // 获取当前动态的所有评论
        PageResult pageResult = commentsApi.queryCommentList(page, pagesize, movementId);

        // 创建 List 集合，用来存储 commentVo 对象
        List<CommentVo> commentVos = new ArrayList<>();

        // 获取每条评论对应的用户信息
        List<Comment> comments = pageResult.getItems();
        if (comments != null) {
            for (Comment comment : comments) {
                // 创建 commentVo 对象
                CommentVo commentVo = new CommentVo();
                BeanUtils.copyProperties(comment, commentVo);

                Long userId = comment.getUserId();
                // 根据 userId 获取用户详细信息
                UserInfo userInfo = userInfoApi.getUserInfo(userId);
                if (userInfo != null) {
                    commentVo.setAvatar(userInfo.getAvatar()); // 头像
                    commentVo.setNickname(userInfo.getNickname()); // 昵称
                }
                commentVo.setCreateDate(new DateTime(comment.getCreated()).toString("yyyy年MM月dd日 HH:mm")); // 创建时间
                commentVo.setId(comment.getId().toHexString());
                commentVo.setHasLiked(0);//是否点赞
                // 将封装好的对象添加到集合中
                commentVos.add(commentVo);
            }
        }
        pageResult.setItems(commentVos);
        return pageResult;
    }

    /**
    * @Desc: 发表评论
    * @Param: [movementId, comment]
    * @return: void
    */
    public void addComment(String publishId, String commentText) {
        // 封装 commment 对象
        Comment comment = new Comment();

        comment.setUserId(UserHolder.getUserId());
        comment.setCommentType(2); // 点赞
        comment.setPubType(1); // 对动态操作
        comment.setPublishId(new ObjectId(publishId));
        comment.setId(new ObjectId());
        comment.setCreated(System.currentTimeMillis());
        comment.setContent(commentText); // 评论内容

        // 获取该条评论的发布人 Id
        Long userId = commentsApi.queryUserIdByPublishId(publishId);
        // 给对象赋值
        comment.setPublishUserId(userId);

        // 向评论表中添加数据
        movementsApi.save(comment);
    }
}