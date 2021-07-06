package com.tanhua.server.controller;

import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.server.service.MovementsService;
import com.tanhua.server.service.UserSettingService;
import org.bson.types.ObjectId;
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

    /**
     * 展示好友动态
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity queryFriendPublishList(Integer page, Integer pagesize){
        PageResult pageResult = movementsService.queryFriendPublishList(page,pagesize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 展示推荐动态
     */
    @RequestMapping(value = "/recommend", method = RequestMethod.GET)
    public ResponseEntity queryRecommendPublishList(Integer page, Integer pagesize){
        PageResult pageResult = movementsService.queryRecommendPublishList(page,pagesize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 我的动态
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity queryMyPublishList(Integer page, Integer pagesize){
        PageResult pageResult = movementsService.queryMyPublishList(page,pagesize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 动态点赞
     */
    @RequestMapping(value = "/{id}/like", method = RequestMethod.GET)
    public ResponseEntity<Long> like(@PathVariable("id") String publishId) {
        Long total = movementsService.like(publishId);
        return ResponseEntity.ok(total);
    }

    /**
     * 动态取消点赞
     */
    @RequestMapping(value = "/{id}/dislike", method = RequestMethod.GET)
        public ResponseEntity<Long> unlike(@PathVariable("id") String publishId){
            Long total = movementsService.unLike(publishId);
            return ResponseEntity.ok(total);
    }
}