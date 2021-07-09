package com.tanhua.server.service;

import com.tanhua.dubbo.api.MessageApi;
import com.tanhua.dubbo.api.UserSettingApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

/**
 * 用户管理业务层
 */
@Service
public class MessageService {

    @Reference
    private MessageApi messageApi;


    public void addFriends(Integer toUserId) {
        // 获取当前用户id
        Long userId = UserHolder.getUserId();

        // 类型转换
        Long friendUserId = toUserId.longValue();

        messageApi.addFriends(userId,friendUserId);
    }
}