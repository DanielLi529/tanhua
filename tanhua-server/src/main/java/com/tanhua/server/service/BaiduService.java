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
public class BaiduService {

    @Reference
    private BaiduApi baiduApi;

    /**
    * @Desc: 上报地理位置
    * @Param: [latitude, longitude, addrStr]
    * @return: void
    */
    public void reportLocation(Double latitude, Double longitude, String addrStr) {
        // 调用方法
        baiduApi.reportLocation(latitude,longitude,addrStr,UserHolder.getUserId());
    }
}