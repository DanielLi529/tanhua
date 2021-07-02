package com.tanhua.server.service;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.TodayBestVo;
import com.tanhua.dubbo.api.TodayBestApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 今日佳人管理业务层
 */
@Service
public class TodayBestService {

    @Reference
    private TodayBestApi todayBestApi;

    @Reference
    private UserInfoApi userInfoApi;

    /**
     * @Desc: 每日佳人
     * @Param: []
     * @return: com.tanhua.domain.vo.TodayBestVo
     */
    public TodayBestVo findTodayBest() {
        // 获取当前登录用户id
        Long userId = UserHolder.getUserId();

        // 创建 todayBestVo 对象
        TodayBestVo todayBestVo = new TodayBestVo();

        // 查询 mongo ,返回用户 id    按理来说应该传入当前日期
        RecommendUser bestUser = todayBestApi.findTodayBest(userId);

        // 判断
        if (bestUser == null) {
            // 没有查询到佳人信息
            bestUser = new RecommendUser();
            bestUser.setUserId(2l);
            bestUser.setScore(95d);
        }

        // 通过 佳人id 查询详细的用户信息
        UserInfo userInfo = userInfoApi.getUserInfo(bestUser.getUserId());

        BeanUtils.copyProperties(userInfo, todayBestVo);

        // 设置缘分值 & tag
        todayBestVo.setFateValue(bestUser.getScore().longValue());

        // 切割 tag
        if (userInfo.getTags() != null) {
            todayBestVo.setTags(userInfo.getTags().split(","));
        }

        return todayBestVo;
    }
}