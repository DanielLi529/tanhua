package com.tanhua.server.controller;

import com.tanhua.domain.mongo.MomentVo;
import com.tanhua.domain.vo.CommentVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.SettingsVo;
import com.tanhua.server.service.CommentsService;
import com.tanhua.server.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 评论控制层
 */
@RestController
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    /**
     * 展示当前动态的评论
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity queryCommentList(String movementId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pagesize) {
        PageResult<CommentVo> pageResult = commentsService.queryCommentList(page, pagesize, movementId);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 发表评论
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity addComment(@RequestBody Map<String, String> map) {
        String movementId = map.get("movementId");
        String comment = map.get("comment");
        System.out.println(movementId + "......." + comment);
        commentsService.addComment(movementId, comment);
        return ResponseEntity.ok(null);
    }

}