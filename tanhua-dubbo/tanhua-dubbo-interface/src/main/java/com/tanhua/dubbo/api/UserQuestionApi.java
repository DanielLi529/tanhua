package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.Settings;


public interface UserQuestionApi {
    /**
     * 展示用户问题设置
     * @param userId
     */
    Question selectQuestion(Long userId);
}
