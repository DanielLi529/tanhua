package com.tanhua.server.controller;

import com.tanhua.domain.vo.PageResult;
import com.tanhua.server.service.MessageService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 用户控制层
 */
@RestController
@RequestMapping("/messages")
public class MessagesController {

    @Autowired
    private MessageService messageService;

    /**
     * 添加好友
     */
    @RequestMapping(value = "/contacts", method = RequestMethod.POST)
    public ResponseEntity addFriends(Integer userId) {
        messageService.addFriends(userId);
        return ResponseEntity.ok(null);
    }

    /**
     * 获取联系人列表
     */
    @RequestMapping(value = "/contacts", method = RequestMethod.GET)
    public ResponseEntity queryFriends(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pagesize,
                                       @RequestParam(required = false) String keyword) {
        PageResult pageResult = messageService.queryFriends(page, pagesize, keyword);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 喜欢列表
     */
    @RequestMapping(value = "/loves", method = RequestMethod.GET)
    public ResponseEntity queryLoveList(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pageResult = messageService.queryLoveList(page, pagesize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 点赞列表
     */
    @RequestMapping(value = "/likes", method = RequestMethod.GET)
    public ResponseEntity queryLikeList(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pageResult = messageService.queryLikeList(page, pagesize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 评论列表
     */
    @RequestMapping(value = "/comments", method = RequestMethod.GET)
    public ResponseEntity queryCommentsList(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pageResult = messageService.queryCommentsList(page, pagesize);
        return ResponseEntity.ok(pageResult);
    }
}