package com.tanhua.server.controller;

import com.tanhua.domain.vo.CountsVo;
import com.tanhua.domain.vo.PageResult;
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
    public ResponseEntity getUserInfo() {
        UserInfoVo userInfoVo = userInfoService.getUserInfo();
        return ResponseEntity.ok(userInfoVo);
    }

    /**
     * 更新用户个人信息
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateUserInfo(@RequestBody UserInfoVo userInfoVo) {
        userInfoService.updateUserInfo(userInfoVo);
        return ResponseEntity.ok(null);
    }

    /**
     * 更新用户头像
     */
    @RequestMapping(value = "/header", method = RequestMethod.POST)
    public ResponseEntity updateUserInfo(MultipartFile headPhoto) {
        userInfoService.updateUserHead(headPhoto);
        return ResponseEntity.ok(null);
    }

    /**
     * 互相喜欢，喜欢，粉丝
     */
    @RequestMapping(value = "/counts", method = RequestMethod.GET)
    public ResponseEntity queryCounts() {
        CountsVo countsVo = userInfoService.queryCounts();
        return ResponseEntity.ok(countsVo);
    }

    /**
     * 互相喜欢，喜欢，粉丝 数据查询
     */
    @RequestMapping(value = "/friends/{type}", method = RequestMethod.GET)
    public ResponseEntity queryFriendsInfo(@PathVariable("type") int type,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int pagesize) {
        PageResult pageResult = userInfoService.queryFriendsInfo(type, page, pagesize);
        return ResponseEntity.ok(pageResult);
    }
}