package com.tanhua.server.controller;

import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.server.service.MovementsService;
import com.tanhua.server.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


/**
 * 圈子控制层
 */
@RestController
@RequestMapping("/movements")
public class MovementsController {

    @Autowired
    private MovementsService movementsService;

    /**
     * 发布动态
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createPublish(PublishVo publishVo, MultipartFile[] imageContent) throws IOException {
        movementsService.createPublish(publishVo,imageContent);
        return ResponseEntity.ok(null);
    }

}