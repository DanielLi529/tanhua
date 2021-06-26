package com.tanhua.server.service;

import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.commons.templates.SmsTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.dubbo.api.UserApi;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理业务层
 */
@Service
public class UserService {
    @Reference
    private UserApi userApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${tanhua.redisValidateCodeKeyPrefix}")
    private String redisValidateCodeKeyPrefix;

    @Autowired
    private SmsTemplate smsTemplate;

    /**
     * 根据手机号查询用户
     *
     * @param mobile
     * @return
     */
    public ResponseEntity findByMobile(String mobile) {
        User user = userApi.findByMobile(mobile);
        return ResponseEntity.ok(user);
    }

    /**
     * 新增用户
     *
     * @param mobile
     * @param password
     * @return
     */
    public ResponseEntity saveUser(String mobile, String password) {
        User user = new User();
        user.setMobile(mobile);
        user.setPassword(password);
        userApi.save(user);
        return ResponseEntity.ok(null);
    }

    /**
     * @Desc: 登陆注册获取验证码
     * @Param: [phone]
     * @return: void
     */
    public void sendValidateCode(String phone) {
        // a.根据手机号查询redis验证码是否失效
        String redisCode = redisTemplate.opsForValue().get("redisValidateCodeKeyPrefix" + phone);

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
        redisTemplate.opsForValue().set("redisValidateCodeKeyPrefix" + phone, validateCode, 5, TimeUnit.MINUTES);
    }
}