package com.tanhua.server.service;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.FollowUser;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.domain.vo.VideoVo;
import com.tanhua.dubbo.api.SmallVideosApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FilterInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SmallVideosService {

    @Reference
    private SmallVideosApi smallVideosApi;

    @Reference
    private UserInfoApi userInfoApi;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    /**
     * @Desc: 上传小视频
     * @Param: [headPhoto, token]
     * @return: void
     */
    public void addSmallVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        // 上传视频封面 ，返回封面的地址
        String picUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoFile.getInputStream());
        // 上传视频
        // 获取上传的视频文件名称
        String originalFilename = videoFile.getOriginalFilename();

        // 截取文件名的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        // 上传文件到 FastDFS
        StorePath storePath = fastFileStorageClient.uploadFile(videoFile.getInputStream(), videoFile.getSize(), suffix, null);

        // 获取文件的完整路径
        String videoUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();

        // 构建 video 对象
        Video video = new Video();

        // 封装数据
        video.setUserId(UserHolder.getUserId());
        video.setText("云怼怼无敌");
        video.setPicUrl(picUrl); // 封面文件的url
        video.setVideoUrl(videoUrl); // 视频文件的url

        // 将对象保存到数据库
        smallVideosApi.saveSmallVideo(video);
    }

    /**
    * @Desc: 分页查询小视频
    * @Param: [page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult<com.tanhua.domain.vo.VideoVo>
    */
    public PageResult<VideoVo> querySmallVideos(Integer page, Integer  pagesize) {

        PageResult pageResult = smallVideosApi.querySmallVideoList(page, pagesize, UserHolder.getUserId());

        // 创建集合存储封装好的 videoVo 对象
        ArrayList<VideoVo> videoVos = new ArrayList<>();

        // 获取分页数据
        List<Video> videos = pageResult.getItems();

        if (videos != null){
            for (Video video : videos) {
                // 创建 videoVo 对象
                VideoVo videoVo = new VideoVo();

                BeanUtils.copyProperties(video,videoVo);

                // 查询小视频作者的详细信息
                UserInfo userInfo = userInfoApi.getUserInfo(video.getUserId());

                if (userInfo != null){
                    BeanUtils.copyProperties(userInfo,videoVo);
                    videoVo.setAvatar(userInfo.getAvatar()); // 头像
                    videoVo.setNickname(userInfo.getNickname()); // 昵称
                }
                // 为对象赋值
                videoVo.setCover(video.getPicUrl()); // 封面
                videoVo.setUserId(video.getUserId()); // 发布人的userId
                // 判断当前登录用户是否已经关注
                String key = "publish_love_" + UserHolder.getUserId() +"_" + video.getUserId();
                if(redisTemplate.opsForValue().get(key) != null){
                    videoVo.setHasFocus(1); // 已关注
                }else {
                    videoVo.setHasFocus(0); // 没有关注
                }

                videoVo.setHasLiked(0); // 是否点赞
                if(video.getText() != null){
                    videoVo.setSignature(video.getText());//签名
                }
                else
                {
                    videoVo.setSignature("默认签名");//签名
                }

                videoVos.add(videoVo);
            }
        }
        pageResult.setItems(videoVos);

        return pageResult;
    }

    /**
    * @Desc: 关注视频用户
    * @Param: [userId]
    * @return: void
    */
    public void followUser(Long toUserId) {

        // 查询当前登录用户是否已关注该视频用户
        Long userId = UserHolder.getUserId();
        List<FollowUser> list = smallVideosApi.queryFollow(userId,toUserId);

        if (list.size() == 0){
            FollowUser followUser = new FollowUser();
            // 构建对象
            followUser.setId(ObjectId.get());
            followUser.setUserId(userId);
            followUser.setFollowUserId(toUserId);
            // 没有则插入数据
            smallVideosApi.saveFollowUser(followUser);

            // 在 redis中保存这条数据
            String key = "publish_love_" + userId +"_" + toUserId;
            redisTemplate.opsForValue().set(key,"1");
        }
    }

    /**
    * @Desc: 取消关注视频用户
    * @Param: [userId]
    * @return: void
    */
    public void unfollowUser(Long toUserId) {
        // 查询当前登录用户是否已关注该视频用户
        Long userId = UserHolder.getUserId();
        List<FollowUser> list = smallVideosApi.queryFollow(userId,toUserId);

        if (list.size() == 0){
            // 没有则删除数据
            smallVideosApi.removeFollowUser(userId,toUserId);

            // 在 redis中保存这条数据
            String key = "publish_love_" + userId +"_" + toUserId;
            redisTemplate.delete(key);
        }
    }
}
