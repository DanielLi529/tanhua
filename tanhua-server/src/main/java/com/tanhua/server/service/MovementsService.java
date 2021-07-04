package com.tanhua.server.service;

import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.commons.templates.SmsTemplate;
import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.dubbo.api.*;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 圈子业务层
 */
@Service
public class MovementsService {

    @Reference
    private MovementsApi movementsApi;

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
        List<String> medias = new ArrayList<String>();
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
}