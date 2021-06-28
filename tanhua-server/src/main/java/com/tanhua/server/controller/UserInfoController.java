package com.tanhua.server.controller;

import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.server.service.UserInfoService;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 用户控制层
 */
@RestController
@RequestMapping("/users")
public class UserInfoController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 查询用户个人信息
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getUserInfo(@RequestHeader("Authorization") String token) {
        UserInfoVo userInfoVo = userInfoService.getUserInfo(token);
        return ResponseEntity.ok(userInfoVo);
    }

    /**
     * 更新用户个人信息
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateUserInfo(@RequestBody UserInfoVo userInfoVo, @RequestHeader("Authorization") String token) {
        userInfoService.updateUserInfo(userInfoVo,token);
        return ResponseEntity.ok(null);
    }

    /**
     * 更新用户头像
     */
    @RequestMapping(value = "/header", method = RequestMethod.POST)
    public ResponseEntity updateUserInfo(MultipartFile headPhoto, @RequestHeader("Authorization") String token) {
        userInfoService.updateUserHead(headPhoto,token);
        return ResponseEntity.ok(null);
    }
}