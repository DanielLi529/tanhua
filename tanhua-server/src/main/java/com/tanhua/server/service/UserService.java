package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.commons.templates.FaceTemplate;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.commons.templates.SmsTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.utils.JwtUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理业务层
 */
@Service
public class UserService {
    @Reference
    private UserApi userApi;

    @Reference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${tanhua.redisValidateCodeKeyPrefix}")
    private String redisValidateCodeKeyPrefix;

    @Value("${tanhua.header}")
    private String header;

    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private FaceTemplate faceTemplate;

    /**
    * @Desc: 通过token获取user对象
    * @Param: [token]
    * @return: com.tanhua.domain.db.User
    */
    public User getUserStr(String token) {
        // 验证 token
        String userStr = redisTemplate.opsForValue().get(header + token);

        if (userStr == null) {
            return null;
        }
        // token 续期
        redisTemplate.expire(header + token,1,TimeUnit.DAYS);

        // json 转 实体类
        return JSON.parseObject(userStr, User.class);
    }

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
        String redisCode = redisTemplate.opsForValue().get(redisValidateCodeKeyPrefix + phone);

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
        redisTemplate.opsForValue().set(redisValidateCodeKeyPrefix + phone, validateCode, 5, TimeUnit.MINUTES);
    }


    /**
    * @Desc: 使用验证码登陆注册
    * @Param: [phone, verificationCode]
    * @return: java.util.Map<java.lang.String,java.lang.Object>
    */
    public Map<String, Object> loginReg(String phone, String verificationCode) {
        // 使用手机号从redis取值
        String redisCode = redisTemplate.opsForValue().get(redisValidateCodeKeyPrefix + phone);

        // 默认非新用户
        boolean isNew = false;

        redisTemplate.delete(redisValidateCodeKeyPrefix + phone);// 删除验证码，防止重复提交
        // 如果取出的值为空
        if (StringUtils.isEmpty(redisCode)) {
            // 登陆超时，验证码已失效
            throw new TanHuaException(ErrorResult.loginError());
        }

        if (!redisCode.equals(verificationCode)) {
            // 用户输入的验证码和redis中的验证码不一致，抛出验证码错误的异常
            throw new TanHuaException(ErrorResult.validateCodeError());
        }
        // 登陆成功，判断是否已经存在该用户（使用mybatis-plus）
        User user = userApi.findByMobile(phone);

        // 如果不存在，则将其保存到数据库中
        if (user == null) {
            user = new User();
            user.setMobile(phone);// 手机号后6位为默认密码
            user.setPassword(DigestUtils.md5Hex(phone.substring(phone.length() - 6)));

            Long userId = userApi.save(user);
            // 给对象ID值
            user.setId(userId);
            isNew = true;
        }

        // 登陆成功  存储token
        String token = jwtUtils.createJWT(phone, user.getId());

        // user 对象转换为Json字符串存入到 redis
        String userStr = JSON.toJSONString(user);
        redisTemplate.opsForValue().set(header + token, userStr, 1, TimeUnit.DAYS);

        // 返回数据
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("isNew", isNew);
        return map;
    }

    /**
    * @Desc: 注册成功存储个人信息
    * @Param: [userInfoVo, token]
    * @return: void
    */
    public void loginRegInfo(UserInfoVo userInfoVo, String token) {
        User user = getUserStr(token);

        if (user == null) {
            throw new TanHuaException(ErrorResult.error());
        }

        // 创建 userinfo 对象
        UserInfo userInfo = new UserInfo();
        // 设置用户id
        Long id = user.getId();
        // 将 userInfoVo 中的属性值拷贝到 userInfo 实体类中
        BeanUtils.copyProperties(userInfoVo, userInfo);
        userInfo.setId(id);
        // token 存的是账户信息，要设置的是用户的基本信息
        userInfoApi.saveUserInfo(userInfo);

    }


    /**
    * @Desc: 注册成功存储头像
    * @Param: [headPhoto, token]
    * @return: void
    */
    public void loginRegHead(MultipartFile headPhoto, String token) {
        try {
            // 验证 token
            User user = getUserStr(token);

            if (user == null) {
                throw new TanHuaException(ErrorResult.error());
            }
            // 获取用户id
            Long id = user.getId();

            // 百度云人脸识别
            boolean detect = faceTemplate.detect(headPhoto.getBytes());

            // 返回结果为true时，说明是人像
            if (!detect){
                throw new TanHuaException(ErrorResult.faceError());
            }

            // 阿里云上传头像
            String filename = headPhoto.getOriginalFilename(); // 获取文件的原始名称
            String headUrl = ossTemplate.upload(filename, headPhoto.getInputStream());

            // 创建 userinfo 对象
            UserInfo userInfo = new UserInfo();
            // 设置用户信息
            userInfo.setId(id);
            userInfo.setAvatar(headUrl);
            // 更新用户信息
            userInfoApi.updateUserInfo(userInfo);
        } catch (IOException e) {
            throw new TanHuaException(ErrorResult.error());
        }
    }
}