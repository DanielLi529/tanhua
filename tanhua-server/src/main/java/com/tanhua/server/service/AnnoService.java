package com.tanhua.server.service;

import com.tanhua.domain.db.Announcement;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.AnnouncementVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.*;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息中心业务层
 */
@Service
public class AnnoService {

    @Reference
    private UserSettingApi userSettingApi;

    @Reference
    private UserQuestionApi userQuestionApi;

    @Reference
    private UserBlackListApi userBlackListApi;

    @Reference
    private AnnoApi annoApi;

    /**
    * @Desc: 展示公告
    * @Param: [page, pagesize]
    * @return: org.springframework.http.ResponseEntity
    */
    public ResponseEntity getAnnoList(int page, int pagesize) {

        PageResult<Announcement> pageResult = annoApi.getAnnoList(page, pagesize);

        // 获取所有的公告对象
        List<Announcement> items = pageResult.getItems();

        // 用来存储 vo对象
        ArrayList<AnnouncementVo> vos = new ArrayList<>();

        for (Announcement item : items) {
            // 创建 annoVo
            AnnouncementVo announcementVo = new AnnouncementVo();
            BeanUtils.copyProperties(item,announcementVo);
            if (item.getCreated() != null){
                // 设置创建日期属性
                announcementVo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item.getCreated()));
            }
            vos.add(announcementVo);
        }

        // 创建 pageResult 对象
        PageResult<AnnouncementVo> announcementVoPageResult = new PageResult<>(pageResult.getCounts(), pageResult.getPagesize(),pageResult.getPages(),pageResult.getPage(),vos);

        return ResponseEntity.ok(announcementVoPageResult);
    }
}