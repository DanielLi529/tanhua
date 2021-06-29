package com.tanhua.server.controller;

import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.server.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

}