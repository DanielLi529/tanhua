package com.itheima.manage.service;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.manage.domain.Admin;
import com.itheima.manage.exception.BusinessException;
import com.itheima.manage.mapper.AdminMapper;
import com.itheima.manage.utils.JwtUtils;
import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.domain.vo.ErrorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.query;

@Service
public class AdminService extends ServiceImpl<AdminMapper, Admin> {

    private static final String CACHE_KEY_CAP_PREFIX = "MANAGE_CAP_";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    /**
    * @Desc: 向redis中存储验证码
    * @Param: [uuid, code]
    * @return: void
    */
    public void saveVerification(String uuid, String code) {
      redisTemplate.opsForValue().set(CACHE_KEY_CAP_PREFIX + uuid, code,10, TimeUnit.MINUTES);
    }

    /**
    * @Desc: 获取登录用户的信息
    * @Param: [token]
    * @return: com.itheima.manage.domain.Admin
    */
    public Admin findUserByToken(String authorization) {
        String token = authorization.replaceFirst("Bearer ","");
        String tokenKey = CACHE_KEY_CAP_PREFIX + token;
        String adminString = (String) redisTemplate.opsForValue().get(tokenKey);
        Admin admin = null;
        if(StringUtils.isNotEmpty(adminString)) {
            admin = JSON.parseObject(adminString, Admin.class);
            // 延长有效期 30分钟
            redisTemplate.expire(tokenKey,30, TimeUnit.MINUTES);
        }
        return admin;
    }

    /**
    * @Desc: 用户登录
    * @Param: [map]
    * @return: java.lang.String
    */
    public Map userLogin(Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        String verificationCode = map.get("verificationCode");
        String uuid = map.get("uuid");

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw  new BusinessException("用户名或者密码为空");
        }

        if(StringUtils.isEmpty(verificationCode) || StringUtils.isEmpty(uuid)) {
            throw  new BusinessException("验证码为空");
        }

        // 校验用户的验证码
        String code = (String) redisTemplate.opsForValue().get(CACHE_KEY_CAP_PREFIX + uuid);
        if (!code.equals(verificationCode) || StringUtils.isEmpty(code)){
            throw new BusinessException("验证码输入错误");
        }

        // 删除验证码
        redisTemplate.delete(CACHE_KEY_CAP_PREFIX + uuid);
        // 校验用户账号
        Admin adminUser = query().eq("username", username).one();

        if (adminUser == null){
            throw new BusinessException("账号不存在");
        }
        if(!adminUser.getPassword().equals(SecureUtil.md5(password))){
            throw new BusinessException("账号或密码错误");
        }

        // 登陆成功  生成 token 并存储
        String token = jwtUtils.createJWT(adminUser.getUsername(), adminUser.getId());
        redisTemplate.opsForValue().set(CACHE_KEY_CAP_PREFIX + token, JSON.toJSONString(adminUser));

        // 返回token
        Map result = new HashMap();
        result.put("token",token);
        return result;
    }
}
