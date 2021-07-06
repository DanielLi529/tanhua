package com.tanhua.server.service;

import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.MomentVo;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.dubbo.api.MovementsApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.RelativeDateFormat;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 圈子业务层
 */
@Service
public class MovementsService {

    @Reference
    private MovementsApi movementsApi;

    @Reference
    private UserInfoApi userInfoApi;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @Desc: 展示用户通用设置
     * @Param: [headPhoto, token]
     * @return: void
     */
    public void createPublish(PublishVo publishVo, MultipartFile[] imageContent) throws IOException {
        // 将图片进行云存储
        // 上传文件到云存储
        List<String> medias = new ArrayList<>();
        for (MultipartFile multipartFile : imageContent) {
            String filename = multipartFile.getOriginalFilename();
            String path = ossTemplate.upload(filename, multipartFile.getInputStream());
            medias.add(path);
        }

        // 设置 publishvo 的 medias 属性（图片） & userId
        publishVo.setUserId(UserHolder.getUserId());
        publishVo.setMedias(medias);

        movementsApi.createPublish(publishVo);
    }

    /**
     * @Desc: 展示好友动态
     * @Param: [page, pagesize]
     * @return: com.tanhua.domain.vo.PageResult
     */
    public PageResult queryFriendPublishList(Integer page, Integer pagesize) {

        List<MomentVo> momentVos = new ArrayList<>();

        // 获取所有的动态信息
        PageResult pageResult = movementsApi.queryFriendPublishList(page,pagesize,UserHolder.getUserId());

        // 循环遍历
        List<Publish> publishes = pageResult.getItems();
        if (publishes != null){
            for (Publish publish : publishes) {

                // 创建 MomentVo 对象
                MomentVo momentVo = new MomentVo();

                if (publish != null && publish.getUserId() != null){
                    // 获取每条动态对应的用户信息
                    UserInfo userInfo = userInfoApi.getUserInfo(publish.getUserId());

                    BeanUtils.copyProperties(userInfo,momentVo);

                    // 设置 tag
                    if (userInfo.getTags() != null){
                        momentVo.setTags(userInfo.getTags().split(","));
                    }
                    BeanUtils.copyProperties(publish,momentVo);

                    // 设置属性
                    momentVo.setId(publish.getId().toHexString());
                    momentVo.setImageContent(publish.getMedias().toArray(new String[]{}));
                    momentVo.setDistance("99米");
                    momentVo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated())));
                    momentVo.setHasLiked(0);
                    momentVo.setHasLoved(0);

                    // 判断当前登录用户是否已经点赞
                    String key = "publish_like_" + UserHolder.getUserId() +"_" + publish.getId();
                    if (redisTemplate.hasKey(key)){
                        momentVo.setHasLiked(1);
                    }else{
                        momentVo.setHasLiked(0);
                    }

                    momentVos.add(momentVo);
                }
            }
            pageResult.setItems(momentVos);
        }
        return pageResult;
    }

    /**
    * @Desc: 展示推荐动态
    * @Param: [page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult
    */
    public PageResult queryRecommendPublishList(Integer page, Integer pagesize) {
        List<MomentVo> momentVos = new ArrayList<>();

        // 获取所有的动态信息
        PageResult pageResult = movementsApi.queryRecommendPublishList(page,pagesize,UserHolder.getUserId());

        // 循环遍历
        List<Publish> publishes = pageResult.getItems();
        if (publishes != null){
            for (Publish publish : publishes) {

                // 创建 MomentVo 对象
                MomentVo momentVo = new MomentVo();

                if (publish != null && publish.getUserId() != null){
                    // 获取每条动态对应的用户信息
                    UserInfo userInfo = userInfoApi.getUserInfo(publish.getUserId());

                    BeanUtils.copyProperties(userInfo,momentVo);

                    // 设置 tag
                    if (userInfo.getTags() != null){
                        momentVo.setTags(userInfo.getTags().split(","));
                    }
                    BeanUtils.copyProperties(publish,momentVo);

                    // 设置属性
                    momentVo.setId(publish.getId().toHexString());
                    momentVo.setImageContent(publish.getMedias().toArray(new String[]{}));
                    momentVo.setDistance("99米");
                    momentVo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated())));
                    momentVo.setHasLiked(0);
                    momentVo.setHasLoved(0);

                    // 判断当前登录用户是否已经点赞
                    String key = "publish_like_" + UserHolder.getUserId() +"_" + publish.getId();
                    if (redisTemplate.hasKey(key)){
                        momentVo.setHasLiked(1);
                    }else{
                        momentVo.setHasLiked(0);
                    }

                    momentVos.add(momentVo);
                }
            }
            pageResult.setItems(momentVos);
        }
        return pageResult;
    }

    /**
    * @Desc: 我的动态
    * @Param: [page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult
    */
    public PageResult queryMyPublishList(Integer page, Integer pagesize) {
        List<MomentVo> momentVos = new ArrayList<>();

        // 获取所有的动态信息
        PageResult pageResult = movementsApi.queryMyPublishList(page,pagesize,UserHolder.getUserId());

        // 循环遍历
        List<Publish> publishes = pageResult.getItems();
        if (publishes != null){
            for (Publish publish : publishes) {

                // 创建 MomentVo 对象
                MomentVo momentVo = new MomentVo();

                if (publish != null && publish.getUserId() != null){
                    // 获取每条动态对应的用户信息
                    UserInfo userInfo = userInfoApi.getUserInfo(publish.getUserId());

                    BeanUtils.copyProperties(userInfo,momentVo);

                    // 设置 tag
                    if (userInfo.getTags() != null){
                        momentVo.setTags(userInfo.getTags().split(","));
                    }
                    BeanUtils.copyProperties(publish,momentVo);

                    // 设置属性
                    momentVo.setId(publish.getId().toHexString());
                    momentVo.setImageContent(publish.getMedias().toArray(new String[]{}));
                    momentVo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated())));
                    momentVo.setHasLiked(0);
                    momentVo.setHasLoved(0);

                    // 判断当前登录用户是否已经点赞
                    String key = "publish_like_" + UserHolder.getUserId() +"_" + publish.getId();
                    if (redisTemplate.hasKey(key)){
                        momentVo.setHasLiked(1);
                    }else{
                        momentVo.setHasLiked(0);
                    }

                    momentVos.add(momentVo);
                }
            }
            pageResult.setItems(momentVos);
        }
        return pageResult;
    }

    /**
    * @Desc: 动态点赞
    * @Param: [id]
    * @return: java.lang.Long
    */
    public Long like(String publishId) {

        // 创建Comment
        Comment comment = new Comment();
        // 设置点赞属性
        comment.setUserId(UserHolder.getUserId());
        comment.setCommentType(1); // 点赞
        comment.setPubType(1); // 对动态操作
        comment.setPublishId(new ObjectId(publishId));
        comment.setId(new ObjectId());
        comment.setCreated(System.currentTimeMillis());

        // 添加评论信息
        Long total = movementsApi.save(comment);

        // 在 redis中保存这条数据
        String key = "publish_like_" + UserHolder.getUserId() +"_" + publishId;
        redisTemplate.opsForValue().set(key,"1");
        return total;
    }

    /**
    * @Desc: 动态取消点赞
    * @Param: [id]
    * @return: java.lang.Long
    */
    public Long unLike(String publishId) {
        // 创建Comment
        Comment comment = new Comment();
        // 设置点赞属性
        comment.setUserId(UserHolder.getUserId());
        comment.setCommentType(1); // 点赞
        comment.setPubType(1); // 对动态操作
        comment.setPublishId(new ObjectId(publishId));
        comment.setId(new ObjectId());
        comment.setCreated(System.currentTimeMillis());

        // 更新动态发布表中对应的 likeCount 数量
        Long total = movementsApi.Remove(comment);
        // 删除redis中的记录
        redisTemplate.delete("publish_like_" + UserHolder.getUserId() +"_" + publishId);

        return total;
    }
}