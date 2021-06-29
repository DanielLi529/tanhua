package com.tanhua.server.service;

import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.db.User;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.UserQuestionApi;
import com.tanhua.dubbo.api.UserSettingApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
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
}