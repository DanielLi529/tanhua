package com.tanhua.server.service;

import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.commons.templates.SmsTemplate;
import com.tanhua.domain.db.Question;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.PageResult;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Reference
    private UserApi userApi;

    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${tanhua.redisValidateCodeKeyPrefix}")
    private String redisValidateCodeKeyPrefix;


    /**
    * @Desc: 展示用户通用设置
    * @Param: [headPhoto, token]
    * @return: void
    */
    public SettingsVo selectSettings() {
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
        // 判断
        if (settings == null){
            settingsVo.setLikeNotification(true);
            settingsVo.setPinglunNotification(true);
            settingsVo.setGonggaoNotification(true);
        }else{
            BeanUtils.copyProperties(settings,settingsVo);
        }

        // 通过id获取用户对象
        String mobile = userApi.findUserById(userId).getMobile();
        // 设置 mobile & question
        settingsVo.setPhone(mobile);
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

    /**
    * @Desc: 更新通知设置
    * @Param: [like, comment, notice]
    * @return: org.springframework.http.ResponseEntity
    */
    public ResponseEntity updateNoticeSetting(Boolean like, Boolean comment, Boolean notice) {
        // 获取用户id
        Long userId = UserHolder.getUserId();

        // 创建对象
        Settings newSetting = new Settings();

        // 查看数据库是否有相关问题设置
        // 查看数据库是否有相关通用设置
        Settings settings = userSettingApi.selectSettings(userId);
        // 存在该条数据
        if (settings != null){
            // 设置属性
            // 设置参数
            newSetting.setLikeNotification(like);
            newSetting.setGonggaoNotification(notice);
            newSetting.setPinglunNotification(comment);

            // 执行 update 的方法
            userSettingApi.updateNoticeSetting(newSetting, userId);
        }else{
            // 不存在，执行 insert 方法
            // 设置属性
            newSetting.setUserId(userId);
            newSetting.setLikeNotification(like);
            newSetting.setGonggaoNotification(notice);
            newSetting.setPinglunNotification(comment);
            userSettingApi.addNoticeSetting(newSetting);
        }

        return ResponseEntity.ok(null);
    }

    /**
     * 修改手机号 > 发送验证码
     */
    public ResponseEntity sendVerificationCode() {
        // 获取用户对象
        String UserMobile = UserHolder.getUser().getMobile();

        System.out.println(UserMobile);
        // a.根据手机号查询redis验证码是否失效
        String redisCode = redisTemplate.opsForValue().get(redisValidateCodeKeyPrefix + UserMobile);

        // b.如果存在，直接返回错误信息，告知“验证码还未失效”
        if (redisCode != null) {
            throw new TanHuaException(ErrorResult.duplicate());
        }

        // 生成验证码
        String validateCode = "123456";
//        String validateCode = RandomStringUtils.randomNumeric(6);

        String stringStringMap = null;
        // 调用短信平台（阿里云）发送验证码短信（手机号码，短信验证码）
//        Map<String, String> stringStringMap = smsTemplate.sendValidateCode(phone, validateCode);

        // c.如果发送成功，则返回的结果为null
        if (stringStringMap != null) {
            // 返回一个未知错误
            throw new TanHuaException(ErrorResult.fail());
        }

        // d.发送成功，将验证码写入redis
        redisTemplate.opsForValue().set(redisValidateCodeKeyPrefix + UserMobile, validateCode, 5, TimeUnit.MINUTES);

        return ResponseEntity.ok(null);
    }

    /**
    * @Desc: 修改手机号 > 校验验证码
    * @Param: [checkCode]
    * @return: org.springframework.http.ResponseEntity
    */
    public Map<String, Boolean> checkVerificationCode(String checkCode) {
        // 获取用户对象
        String UserMobile = UserHolder.getUser().getMobile();

        // 创建map集合
        Map<String, Boolean> map = new HashMap<String, Boolean>();


        // 通过用户手机号码从redis中取值
        String redisCode = redisTemplate.opsForValue().get(redisValidateCodeKeyPrefix + UserMobile);

        // 判断
        if (!checkCode.equals(redisCode)){
            map.put("verification",false);
            return map;
        }
        map.put("verification",true);

        // 为防止重复利用该验证码，校验成功之后删除
        redisTemplate.delete(redisValidateCodeKeyPrefix + UserMobile);// 删除验证码，防止重复提交

        return map;
    }

    /**
    * @Desc: 修改手机号 > 保存新的手机号码
    * @Param: []
    * @return: org.springframework.http.ResponseEntity
    */
    public ResponseEntity updateMobile(String newMobile) {
        // 获取用户id
        Long userId = UserHolder.getUserId();

        // 创建对象
        User user = new User();

        // 设置属性
        user.setMobile(newMobile);

        userApi.updateUserInfo(user, userId);
        return ResponseEntity.ok(null);
    }
}