package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import com.tanhua.dubbo.mapper.UserSettingMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserSettingApiImpl implements UserSettingApi {

    @Autowired
    private UserSettingMapper userSettingMapper;


    /**
     * 展示用户通用设置
     * @param userId
     */
    @Override
    public Settings selectSettings(Long userId) {
        QueryWrapper<Settings> settingsQueryWrapper = new QueryWrapper<>();
        settingsQueryWrapper.eq("user_id",userId);
        return userSettingMapper.selectOne(settingsQueryWrapper);
    }
}
