package com.tanhua.server.service;

import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.MomentVo;
import com.tanhua.domain.mongo.Publish;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.dubbo.api.MovementsApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.RelativeDateFormat;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

                    momentVos.add(momentVo);
                }
            }
            pageResult.setItems(momentVos);
        }
        return pageResult;
    }
}