package com.tanhua.server.controller;

import com.tanhua.domain.vo.RecommendUserQueryParam;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.domain.vo.TodayBestVo;
import com.tanhua.server.service.TodayBestService;
import com.tanhua.server.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 用户控制层
 */
@RestController
@RequestMapping("/tanhua")
public class TodayBestController {

    @Autowired
    private TodayBestService todayBestService;

    /**
     * 今日佳人
     */
    @RequestMapping(value = "/todayBest", method = RequestMethod.GET)
    public ResponseEntity findTodayBest() {
        // 调用 service
        return ResponseEntity.ok(todayBestService.findTodayBest());
    }

    /**
     * 首页推荐
     */
    @RequestMapping(value = "/recommendation", method = RequestMethod.GET)
    public ResponseEntity findRecommendation(RecommendUserQueryParam param) {
        // 调用 service
        return ResponseEntity.ok(todayBestService.findRecommendation(param));
    }
}