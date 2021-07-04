package com.tanhua.server.controller;

import com.tanhua.server.service.AnnoService;
import com.tanhua.server.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 消息控制层
 */
@RestController
@RequestMapping("/messages")
public class AnnoController {

    @Autowired
    private AnnoService annoService;

    /**
     * 展示公告中心
     */
    @RequestMapping(value = "/announcements", method = RequestMethod.GET)
    public ResponseEntity getAnnoList(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10")  int pagesize) {
        return annoService.getAnnoList(page,pagesize);
    }
}