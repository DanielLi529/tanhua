package com.tanhua.server.controller;

import com.tanhua.server.service.MessageService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户控制层
 */
@RestController
@RequestMapping("/messages")
public class MessagesController {

    @Autowired
    private MessageService messageService;

    /**
     * 展示用户通用设置
     */
    @RequestMapping(value = "/contacts", method = RequestMethod.POST)
    public ResponseEntity addFriends(Integer userId) {
        messageService.addFriends(userId);
        return ResponseEntity.ok(null);
    }
}