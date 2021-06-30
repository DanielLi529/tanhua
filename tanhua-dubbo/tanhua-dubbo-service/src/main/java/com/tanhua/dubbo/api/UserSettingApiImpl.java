package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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

    /**
     * @Desc: 更新通知设置
     * @Param: [like, comment, notice]
     * @return: org.springframework.http.ResponseEntity
     */
    @Override
    public void updateNoticeSetting(Settings settings, Long userId) {
        UpdateWrapper<Settings> settingsUpdateWrapper = new UpdateWrapper<>();
        settingsUpdateWrapper.eq("user_id",userId);

        userSettingMapper.update(settings,settingsUpdateWrapper);

    }

    /**
    * @Desc: 插入通知设置信息
    * @Param: [newSetting]
    * @return: void
    */
    @Override
    public void addNoticeSetting(Settings newSetting) {
        userSettingMapper.insert(newSetting);
    }
}
