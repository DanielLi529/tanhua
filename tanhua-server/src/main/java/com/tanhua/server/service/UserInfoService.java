package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.commons.templates.FaceTemplate;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.vo.CountsVo;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.dubbo.api.CommentsApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.TanhuaServerApplication;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.GetAgeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserInfoService {

    @Reference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${tanhua.header}")
    private String header;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private FaceTemplate faceTemplate;

    @Autowired
    private UserService userService;

    /**
     * @Desc: 获取用户基本信息
     * @Param: [token]
     * @return: org.springframework.http.ResponseEntity
     */
    public UserInfoVo getUserInfo() {
        // 获取用户 id
        Long id = UserHolder.getUserId();

        // 获取用户信息
        UserInfo userInfo = userInfoApi.getUserInfo(id);

        // 通过birthday，计算用户的年龄
        int age = GetAgeUtil.getAge(userInfo.getBirthday());
        userInfo.setAge(age);//设置年龄
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo,userInfoVo);
        // 单独设置年龄
        userInfoVo.setAge(String.valueOf(userInfo.getAge()));
        return userInfoVo;
    }

    /**
    * @Desc: 更新用户信息
    * @Param: [userInfoVo]
    * @return: void
    */
    public void updateUserInfo(UserInfoVo userInfoVo) {
        // 获取用户 id
        Long id = UserHolder.getUserId();

        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfoVo,userInfo);

        // 更新生日的同时需要更新年龄
        if(!StringUtils.isEmpty(userInfoVo.getAge())){
            userInfo.setAge(GetAgeUtil.getAge(userInfo.getBirthday()));
        }
        userInfo.setId(id);
        userInfoApi.updateUserInfo(userInfo);
    }

    /**
    * @Desc: 更新用户头像
    * @Param: [headPhoto]
    * @return: void
    */
    public void updateUserHead(MultipartFile headPhoto) {
        try {
            // 获取用户 id
            Long id = UserHolder.getUserId();

            // 百度云人脸识别
            boolean detect = faceTemplate.detect(headPhoto.getBytes());

            // 返回结果为true时，说明是人像
            if (!detect){
                throw new TanHuaException(ErrorResult.faceError());
            }

            // 阿里云上传头像
            String filename = headPhoto.getOriginalFilename(); // 获取文件的原始名称
            String headUrl = ossTemplate.upload(filename, headPhoto.getInputStream());

            // 创建 userinfo 对象
            UserInfo userInfo = new UserInfo();
            // 设置用户信息
            userInfo.setId(id);
            userInfo.setAvatar(headUrl);

            userInfoApi.updateUserHead(userInfo);
        } catch (IOException e) {
            throw new TanHuaException(ErrorResult.error());
        }
    }

    /**
    * @Desc: 互相喜欢，喜欢，粉丝 数量
    * @Param: []
    * @return: com.tanhua.domain.vo.CountsVo
    */
    public CountsVo queryCounts() {
        Long userId = UserHolder.getUserId();
        // 获取喜欢的数量
        Long loveCount = userInfoApi.queryLoveCount(userId);

        // 获取粉丝的数量
        Long fansCount = userInfoApi.queryFansCount(userId);

        // 获取好友的数量
        Long mutualCount = userInfoApi.queryEachLoveCount(userId);

        // 创建对象
        CountsVo countsVo = new CountsVo();
        countsVo.setLoveCount(loveCount);
        countsVo.setFanCount(fansCount);
        countsVo.setEachLoveCount(mutualCount);
        return countsVo;
    }
}
