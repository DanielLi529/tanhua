package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.Settings;
import com.tanhua.dubbo.mapper.UserQuestionMapper;
import com.tanhua.dubbo.mapper.UserSettingMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserQuestionApiImpl implements UserQuestionApi {

    @Autowired
    private UserQuestionMapper userQuestionMapper;

    /**
    * @Desc: 展示用户问题设置
    * @Param: [userId]
    * @return: com.tanhua.domain.db.Question
    */
    @Override
    public Question selectQuestion(Long userId) {
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("user_id",userId);
        return userQuestionMapper.selectOne(questionQueryWrapper);
    }
}
