package com.tanhua.server.controller;

import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.server.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 用户控制层
 */
@RestController
@RequestMapping("/users")
public class UserSettingController {

    @Autowired
    private UserSettingService userSettingService;

    /**
     * 展示用户通用设置
     */
    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public ResponseEntity selectSettings() {
        SettingsVo newSettingVo = userSettingService.selectSettings();
        return ResponseEntity.ok(newSettingVo);
    }

    /**
     * 设置陌生人问题
     */
    @RequestMapping(value = "/questions", method = RequestMethod.POST)
    public ResponseEntity setQuestion(@RequestBody Map map) {
        // 获取输入内容
        String content = (String) map.get("content");
        System.out.println(content);

        userSettingService.setQuestion(content);
        return ResponseEntity.ok(null);
    }

    /**
     * 展示黑名单
     */
    @RequestMapping(value = "/blacklist", method = RequestMethod.GET)
    public ResponseEntity getBlackList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10")  int pagesize) {
        return userSettingService.getBlackList(page,pagesize);
    }

    /**
     * 移除黑名单
     */
    @RequestMapping(value = "/blacklist/{uid}}", method = RequestMethod.DELETE)
    public ResponseEntity deleteBlackList(@PathVariable("uid") long delId) {
        System.out.println(delId);
        return userSettingService.deleteBlackList(delId);
    }
}