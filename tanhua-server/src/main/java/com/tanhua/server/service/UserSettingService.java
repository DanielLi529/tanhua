package com.tanhua.server.service;

import com.sun.org.apache.regexp.internal.RE;
import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.dubbo.api.*;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * 用户管理业务层
 */
@Service
public class UserSettingService {

    @Reference
    private UserSettingApi userSettingApi;

    @Reference
    private UserQuestionApi userQuestionApi;

    @Reference
    private UserBlackListApi userBlackListApi;


    /**
    * @Desc: 展示用户通用设置
    * @Param: [headPhoto, token]
    * @return: void
    */
    public SettingsVo selectSettings() {
        // 获取用户对象
        User user = UserHolder.getUser();
        // 获取用户id
        Long userId = UserHolder.getUserId();

        // 创建 settingVo 对象
        SettingsVo settingsVo = new SettingsVo();

        // 查看数据库是否有相关问题设置
        Question question = userQuestionApi.selectQuestion(userId);
        String StrangerQuestion = "约吗？";
        if (question != null && question.getTxt() != null){
            StrangerQuestion = question.getTxt();
        }

        // 查看数据库是否有相关通用设置
        Settings settings = userSettingApi.selectSettings(userId);
        if (settings == null){
            settingsVo.setLikeNotification(true);
            settingsVo.setPinglunNotification(true);
            settingsVo.setGonggaoNotification(true);
        }else{
            BeanUtils.copyProperties(settings,settingsVo);
        }

        // 设置 mobile & question
        settingsVo.setPhone(user.getMobile());
        settingsVo.setStrangerQuestion(StrangerQuestion);
        return settingsVo;
    }

    /**
    * @Desc: 设置陌生人问题
    * @Param: [content]
    * @return: void
    */
    public void setQuestion(String content) {
        // 获取用户id
        Long userId = UserHolder.getUserId();

        // 创建对象
        Question qt = new Question();

        // 查看数据库是否有相关问题设置
        Question question = userQuestionApi.selectQuestion(userId);
        // 存在该条数据
        if (question != null && question.getTxt() != null){
            // 设置属性
            qt.setTxt(content);

            // 执行 update 的方法
            userQuestionApi.updateQuestion(qt, userId);
        }else{
            // 不存在，执行 insert 方法
            // 设置属性
            qt.setUserId(userId);
            qt.setTxt(content);
            userQuestionApi.addQuestion(qt);
        }
    }

    /**
    * @Desc: 展示黑名单
    * @Param: [page, pagesize]
    * @return: org.springframework.http.ResponseEntity
    */
    public ResponseEntity getBlackList(int page, int pagesize) {
        // 获取用户id
        Long userId = UserHolder.getUserId();

        PageResult<UserInfo> pageResult = userBlackListApi.getBlackList(page, pagesize, userId);
        return ResponseEntity.ok(pageResult);
    }

    /**
    * @Desc: 移除黑名单
    * @Param: [deleteUserId]
    * @return: org.springframework.http.ResponseEntity
    */
    public ResponseEntity deleteBlackList(long deleteUserId) {
        // 获取用户id
        Long userId = UserHolder.getUserId();

        userBlackListApi.deleteBlackList(deleteUserId, userId);

        return ResponseEntity.ok(null);
    }
}