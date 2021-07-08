package com.tanhua.server.controller;

import com.tanhua.server.service.SmallVideosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * 小视频控制层
 */
@RestController
@RequestMapping("/smallVideos")
public class smallVideosController {

    @Autowired
    private SmallVideosService smallVideosService;

    /**
     * 上传小视频
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity addSmallVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        smallVideosService.addSmallVideos(videoThumbnail, videoFile);
        return ResponseEntity.ok(null);
    }

}