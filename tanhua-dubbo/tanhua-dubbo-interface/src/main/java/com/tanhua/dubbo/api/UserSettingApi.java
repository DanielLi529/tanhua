package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.Settings;


public interface UserSettingApi {
    /**
     * 展示用户通用设置
     * @param userId
     */
    Settings selectSettings(Long userId);

    /**
     * @Desc: 更新通知设置
     * @Param: [like, comment, notice]
     * @return: org.springframework.http.ResponseEntity
     */
    void updateNoticeSetting(Settings settings, Long userId);

    void addNoticeSetting(Settings newSetting);
}
