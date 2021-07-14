package com.itheima.manage.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.itheima.manage.domain.Admin;
import com.itheima.manage.interceptor.AdminHolder;
import com.itheima.manage.service.AdminService;
import com.itheima.manage.vo.AdminVo;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/system/users")
@Log4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * @Desc: 用户登录验证码图片
     * @Param:
     * @return:
     */
    @RequestMapping(value = "/verification", method = RequestMethod.GET)
    public void getVerification(String uuid, HttpServletRequest req, HttpServletResponse res) {
        res.setDateHeader("Expires", 0);
        res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        res.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        res.setHeader("Pragma", "no-cache");
        res.setContentType("image/jpeg");

        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(299, 97);
        String code = lineCaptcha.getCode();

        adminService.saveVerification(uuid, code);
        System.out.println(code);
        try {
            lineCaptcha.write(res.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * @Desc: 用户登录
    * @Param: [map]
    * @return: org.springframework.http.ResponseEntity
    */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity userLogin(@RequestBody Map<String, String> map) {
        return ResponseEntity.ok(adminService.userLogin(map));
    }

    /**
    * @Desc: 获取当前登录用户信息
    * @Param: []
    * @return: org.springframework.http.ResponseEntity
    */
    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ResponseEntity profile() {
        Admin admin = AdminHolder.getAdmin();
        AdminVo vo = new AdminVo();
        BeanUtils.copyProperties(admin, vo);
        return ResponseEntity.ok(vo);
    }
}
