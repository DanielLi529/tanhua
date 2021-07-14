package com.tanhua.server.controller;

import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.server.service.BaiduService;
import com.tanhua.server.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 用户控制层
 */
@RestController
@RequestMapping("/baidu")
public class BaiduController {

    @Autowired
    private BaiduService baiduService;

    /**
     * 展示用户通用设置
     */
    @RequestMapping(value = "/location", method = RequestMethod.POST)
    public ResponseEntity reportLocation(@RequestBody Map map) {
        Double latitude = (Double)map.get("latitude");  //
        Double longitude = (Double)map.get("longitude");
        String addrStr = (String) map.get("addrStr");
        baiduService.reportLocation(latitude, longitude, addrStr);
        return ResponseEntity.ok(null);
    }
}