package com.tanhua.server.service;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.vo.ContactVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.MessageApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.UserSettingApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理业务层
 */
@Service
public class MessageService {

    @Reference
    private MessageApi messageApi;

    @Reference
    private UserInfoApi userInfoApi;

    /**
    * @Desc: 添加好友
    * @Param: [toUserId]
    * @return: void
    */
    public void addFriends(Integer toUserId) {
        // 获取当前用户id
        Long userId = UserHolder.getUserId();

        // 类型转换
        Long friendUserId = toUserId.longValue();

        messageApi.addFriends(userId,friendUserId);
    }

    /**
    * @Desc: 获取好友列表
    * @Param: [page, pagesize, keyword]
    * @return: com.tanhua.domain.vo.PageResult
    */
    public PageResult queryFriends(Integer page, Integer pagesize, String keyword) {
        // 获取查询结果
        PageResult pageResult = messageApi.queryFriends(page, pagesize, keyword,UserHolder.getUserId());

        // 创建集合存储要返回的用户信息
        List<ContactVo> list = new ArrayList<>();
        // 获取好友对象
        List<Friend> friends = pageResult.getItems();

        if (friends != null){
            for (Friend friend : friends) {

                // 创建对象
                ContactVo contactVo = new ContactVo();

                // 通过 userid 查询对应的用户信息
                Long friendId = friend.getFriendId();
                UserInfo userInfo = userInfoApi.getUserInfo(friendId);

                BeanUtils.copyProperties(userInfo,contactVo);

                contactVo.setUserId(friendId.toString());

                // 将对象添加到集合中
                list.add(contactVo);
            }
            pageResult.setItems(list);
        }

        // 返回数据
        return pageResult;
    }
}