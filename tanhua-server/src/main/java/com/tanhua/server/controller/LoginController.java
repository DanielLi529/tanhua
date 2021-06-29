package com.tanhua.server.controller;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.UserInfoVo;
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
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 根据手机号查询用户
     */
    @RequestMapping(value = "/findUser", method = RequestMethod.GET)
    public ResponseEntity findUser(String mobile) {
        return userService.findByMobile(mobile);
    }

    /**
     * 新增用户
     */
    @RequestMapping(value = "/saveUser", method = RequestMethod.POST)
    public ResponseEntity saveUser(@RequestBody Map<String, Object> param) {
        String mobile = (String) param.get("mobile");
        String password = (String) param.get("password");
        return userService.saveUser(mobile, password);
    }


    /**
     * @Desc: 登陆注册发送验证码
     * @Param: [mobile]
     * @return: org.springframework.http.ResponseEntity
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity sendValidateCode(@RequestBody Map<String, String> params) {
        // 从参数中获取手机号
        String phone = params.get("phone");
        // 调用service层的方法，获取结果
        userService.sendValidateCode(phone);
        // 告诉前端结果，是成功的即可，不需要要给他返回数据
        return ResponseEntity.ok(null);
    }

    /**
    * @Desc: 使用手机验证码登录注册
    * @Param: [params]
    * @return: org.springframework.http.ResponseEntity
    */
    @RequestMapping(value = "/loginVerification", method = RequestMethod.POST)
    public ResponseEntity loginReg(@RequestBody Map<String, String> params){
        // 从参数中获取手机号和验证码
        String phone = params.get("phone");
        String verificationCode = params.get("verificationCode");
        // 调用service层的方法，获取结果
        Map<String,Object> map = userService.loginReg(phone,verificationCode);
        // 告诉前端结果,(为什么要返回结果？)头像等信息吗？
        return ResponseEntity.ok(map);
    }

    /**
    * @Desc: 保存用户信息
    * @Param: [userInfoVo, token]
    * @return: org.springframework.http.ResponseEntity
    */
    @RequestMapping(value = "/loginReginfo", method = RequestMethod.POST)
    public ResponseEntity loginRegInfo(@RequestBody UserInfoVo userInfoVo){
        userService.loginRegInfo(userInfoVo);
        // 告诉前端结果
        return ResponseEntity.ok(null);
    }


    // 设置用户头像
    @RequestMapping(value = "/loginReginfo/head", method = RequestMethod.POST)
    public ResponseEntity loginRegHead(MultipartFile headPhoto){
        userService.loginRegHead(headPhoto);
        // 告诉前端结果
        return ResponseEntity.ok(null);
    }
}