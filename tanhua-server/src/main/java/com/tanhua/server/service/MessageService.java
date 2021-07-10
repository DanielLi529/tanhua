package com.tanhua.server.service;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.vo.ContactVo;
import com.tanhua.domain.vo.MessageVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.MessageApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.UserSettingApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.joda.time.DateTime;
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

    /**
    * @Desc: 获取评论列表
    * @Param: [page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult
    */
    public PageResult queryLoveList(Integer page, Integer pagesize, Integer commentType) {
        // 查询点赞的用户 id
        PageResult pageResult = messageApi.queryLoveList(page, pagesize, UserHolder.getUserId(), commentType);

        // 创建集合 用来存储返回值对象
        ArrayList<MessageVo> messageVos = new ArrayList<>();

        // 获取所有的评论对象
        List<Comment> comments = pageResult.getItems();
        if (comments != null){
            for (Comment comment : comments) {
                // 创建返回值对象
                MessageVo messageVo = new MessageVo();
                // 使用评论人的 UserId 来获取他的详细信息
                UserInfo userInfo = userInfoApi.getUserInfo(comment.getUserId());
                // 为对象赋值
                messageVo.setId(userInfo.getId().toString());
                messageVo.setAvatar(userInfo.getAvatar());
                messageVo.setNickname(userInfo.getNickname());
                messageVo.setCreateDate(new DateTime(comment.getCreated()).toString("yyyy年MM月dd日 HH:mm"));

                // 添加到集合中
                messageVos.add(messageVo);
            }
            pageResult.setItems(messageVos);
        }

        return pageResult;
    }
}