package com.tanhua.server.controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.commons.vo.HuanXinUser;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 用户控制层
 */
@RestController
@RequestMapping("/huanxin")
public class HuanXinController {

    /**
     * 获取登陆环信得用户账号密码
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<HuanXinUser> getLoginHuanXinUser() {
        HuanXinUser huanXinUser = new HuanXinUser(UserHolder.getUserId().toString(), "123456", String.format("cp dd"));
        return ResponseEntity.ok(huanXinUser);
    }
}