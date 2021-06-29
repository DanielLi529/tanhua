package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.PageResult;


public interface UserQuestionApi {
    /**
     * 展示用户问题设置
     * @param userId
     */
    Question selectQuestion(Long userId);

    /**
    * @Desc: 设置用户问题
    * @Param: [userId, content]
    * @return: void
    */

    void updateQuestion(Question question,Long userId);

    void addQuestion(Question question);
}
