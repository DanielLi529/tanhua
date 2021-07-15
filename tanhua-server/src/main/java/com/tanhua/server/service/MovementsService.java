package com.tanhua.server.service;

import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.MomentVo;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.mongo.Visitor;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.domain.vo.VisitorVo;
import com.tanhua.dubbo.api.CommentsApi;
import com.tanhua.dubbo.api.MovementsApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.RelativeDateFormat;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.MessagingException;
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

    @Reference
    private CommentsApi commentsApi;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * @Desc: 发布动态
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

        // 添加发布信息并返回发布Id
         String publishId = movementsApi.createPublish(publishVo);

        // 创建该条动态之后将对应的发布Id存储到消息队列,指定 生产者组 以及要 上传的内容
        rocketMQTemplate.convertAndSend("tanhua_publish", publishId);
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
                    // 判断当前登录用户是否已经喜欢
                    String loveKey = "publish_love_" + UserHolder.getUserId() +"_" + publish.getId();
                    if (redisTemplate.hasKey(loveKey)){
                        momentVo.setHasLoved(1);
                    }else{
                        momentVo.setHasLoved(0);
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
                    // 判断当前登录用户是否已经喜欢
                    String loveKey = "publish_love_" + UserHolder.getUserId() +"_" + publish.getId();
                    if (redisTemplate.hasKey(loveKey)){
                        momentVo.setHasLoved(1);
                    }else{
                        momentVo.setHasLoved(0);
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
    public PageResult queryMyPublishList(Integer page, Integer pagesize,Integer userId) {
        List<MomentVo> momentVos = new ArrayList<>();

        // 获取所有的动态信息
        PageResult pageResult = movementsApi.queryMyPublishList(page,pagesize,userId.longValue());

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

                    // 判断当前登录用户是否已经喜欢
                    String loveKey = "publish_love_" + UserHolder.getUserId() +"_" + publish.getId();
                    if (redisTemplate.hasKey(loveKey)){
                        momentVo.setHasLoved(1);
                    }else{
                        momentVo.setHasLoved(0);
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

        // 获取该条评论的发布人 Id
        Long userId = commentsApi.queryUserIdByPublishId(publishId);
        // 给对象赋值
        comment.setPublishUserId(userId);

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

    /**
     * @Desc: 动态喜欢
     * @Param: [id]
     * @return: java.lang.Long
     */
    public Long love(String publishId) {

        // 创建Comment
        Comment comment = new Comment();
        // 设置点赞属性
        comment.setUserId(UserHolder.getUserId());
        comment.setCommentType(3); // 点赞
        comment.setPubType(1); // 对动态操作
        comment.setPublishId(new ObjectId(publishId));
        comment.setId(new ObjectId());
        comment.setCreated(System.currentTimeMillis());

        // 获取该条评论的发布人 Id
        Long userId = commentsApi.queryUserIdByPublishId(publishId);
        // 给对象赋值
        comment.setPublishUserId(userId);

        // 添加评论信息
        Long total = movementsApi.save(comment);

        // 在 redis中保存这条数据
        String key = "publish_love_" + UserHolder.getUserId() +"_" + publishId;
        redisTemplate.opsForValue().set(key,"1");
        return total;
    }

    /**
     * @Desc: 动态取消喜欢
     * @Param: [id]
     * @return: java.lang.Long
     */
    public Long unlove(String publishId) {
        // 创建Comment
        Comment comment = new Comment();
        // 设置点赞属性
        comment.setUserId(UserHolder.getUserId());
        comment.setCommentType(3); // 点赞
        comment.setPubType(1); // 对动态操作
        comment.setPublishId(new ObjectId(publishId));
        comment.setId(new ObjectId());
        comment.setCreated(System.currentTimeMillis());

        // 更新动态发布表中对应的 likeCount 数量
        Long total = movementsApi.Remove(comment);
        // 删除redis中的记录
        redisTemplate.delete("publish_love_" + UserHolder.getUserId() +"_" + publishId);

        return total;
    }


    /**
     * @Desc: 获取单个动态的评论
     * @Param: [movementId]
     * @return: com.tanhua.domain.vo.PageResult<com.tanhua.domain.vo.CommentVo>
     */
    public MomentVo queryComment(String publishId) {
        // 获取当前动态信息
        Publish publish = movementsApi.queryPublishById(publishId);

        // 创建 momentVo 对象
        MomentVo momentVo = new MomentVo();

        BeanUtils.copyProperties(publish, momentVo);
        // 通过动态信息获取当前用户的个人信息
        if (publish != null) {
            UserInfo userInfo = userInfoApi.getUserInfo(publish.getUserId());

            if (userInfo != null) {
                BeanUtils.copyProperties(userInfo, momentVo);
                if (userInfo.getTags() != null) {
                    momentVo.setTags(userInfo.getTags().split(","));
                }
            }
        }

        // 给 MomentVo 对象赋值
        momentVo.setId(publish.getId().toHexString());
        momentVo.setImageContent(publish.getMedias().toArray(new String[]{}));
        momentVo.setDistance("0");
        momentVo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated())));
        momentVo.setHasLiked(0);  //是否点赞  0：未点 1:点赞
        momentVo.setHasLoved(0);  //是否喜欢  0：未点 1:点赞

        // 返回数据
        return momentVo;
    }

    /**
    * @Desc: 谁看过我
    * @Param: []
    * @return: com.tanhua.domain.vo.VisitorVo
    */
    public List<VisitorVo> queryVisitors() {
        // 我们会将用户每次登陆的时间保存至 redis 中
        // 先判断用户是否登陆过，再决定需要查询的数据内容
        String key = "visitors_time_" + UserHolder.getUserId();
        String lastTime = (String)redisTemplate.opsForValue().get(key);



        List<Visitor> visitors = new ArrayList<>();
        if (redisTemplate.opsForValue().get(key) == null){
            // 没有登陆过 最近五天的
            visitors = movementsApi.queryVisitors(UserHolder.getUserId());
        }else{
            // 登陆过  则获取最后一次登录至今记录中的五条数据
            visitors = movementsApi.queryLastVisitors(UserHolder.getUserId(),lastTime);
        }

        // 在redis中记录用户本次的访问时间
        redisTemplate.opsForValue().set(key,System.currentTimeMillis()+"");

        // 创建集合存储返回值对象
        ArrayList<VisitorVo> visitorVos = new ArrayList<>();
        if (visitors != null){
            // 不为空则copy属性
            for (Visitor visitor : visitors) {
                // 创建对象
                VisitorVo visitorVo = new VisitorVo();
                BeanUtils.copyProperties(visitor,visitorVo);

                // 查询用户的详细信息
                UserInfo userInfo = userInfoApi.getUserInfo(visitor.getVisitorUserId());
                BeanUtils.copyProperties(userInfo, visitorVo);

                // 设置标签
                if (userInfo.getTags() != null) {
                    visitorVo.setTags(userInfo.getTags().split(","));
                }

                // 为对象赋值
                if (visitor.getScore() != null){
                    visitorVo.setFateValue(visitor.getScore().intValue());
                }

                visitorVos.add(visitorVo);
            }

        }
        return visitorVos;
    }
}