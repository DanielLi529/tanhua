package com.tanhua.server.service;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.domain.mongo.Video;
import com.tanhua.domain.vo.PublishVo;
import com.tanhua.dubbo.api.SmallVideosApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FilterInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SmallVideosService {

    @Reference
    private SmallVideosApi smallVideosApi;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    /**
     * @Desc: 上传小视频
     * @Param: [headPhoto, token]
     * @return: void
     */
    public void addSmallVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        // 上传视频封面 ，返回封面的地址
        String picUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoFile.getInputStream());
        // 上传视频
        // 获取上传的视频文件名称
        String originalFilename = videoFile.getOriginalFilename();

        // 截取文件名的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        // 上传文件到 FastDFS
        StorePath storePath = fastFileStorageClient.uploadFile(videoFile.getInputStream(), videoFile.getSize(), suffix, null);

        // 获取文件的完整路径
        String videoUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();

        // 构建 video 对象
        Video video = new Video();

        // 封装数据
        video.setUserId(UserHolder.getUserId());
        video.setText("云怼怼无敌");
        video.setPicUrl(picUrl); // 封面文件的url
        video.setVideoUrl(videoUrl); // 视频文件的url

        // 将对象保存到数据库
        smallVideosApi.saveSmallVideo(video);
    }
}
