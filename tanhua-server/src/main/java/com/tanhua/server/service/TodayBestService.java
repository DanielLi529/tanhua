package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.RecommendUser;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.RecommendUserQueryParam;
import com.tanhua.domain.vo.TodayBestVo;
import com.tanhua.dubbo.api.RecommendApi;
import com.tanhua.dubbo.api.TodayBestApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 今日佳人管理业务层
 */
@Service
public class TodayBestService {

    @Reference
    private TodayBestApi todayBestApi;

    @Reference
    private UserInfoApi userInfoApi;

    @Reference
    private RecommendApi recommendApi;

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

    /**
     * @Desc: 首页推荐
     * @Param: []
     * @return: com.tanhua.domain.vo.TodayBestVo
     */
    public PageResult<TodayBestVo> findRecommendation(RecommendUserQueryParam param) {

        // 获取当前登录用户id
        Long userId = UserHolder.getUserId();

        // 创建 list 集合
        List<TodayBestVo> todayBestVos = new ArrayList<>();

        // 从数据库中取值
        PageResult pageResult = recommendApi.findRecommendation(param.getPage(),param.getPagesize(),userId);

        List<RecommendUser> items = pageResult.getItems();

        // 判断是否有推荐用户
        if (CollectionUtils.isEmpty(items)) {
            pageResult = new PageResult(10l,param.getPagesize().longValue(),1l,1l,null);
            items = defaultRecommend();
        }

        // 循环遍历
        for (RecommendUser item : items) {
            // 创建 todayBest 对象
            TodayBestVo todayBestVo = new TodayBestVo();

            // 获取推荐用户的详细信息
            UserInfo userInfo = userInfoApi.getUserInfo(item.getUserId());

            BeanUtils.copyProperties(userInfo,todayBestVo);

            // 设置id & 缘分值 & tag
            todayBestVo.setId(item.getUserId());
            todayBestVo.setFateValue(item.getScore().longValue());

            // 切割 tag
            if (userInfo.getTags() != null) {
                todayBestVo.setTags(userInfo.getTags().split(","));
            }

            todayBestVos.add(todayBestVo);
        }

        pageResult.setItems(todayBestVos);
//
//        // 为对象赋值
//        todayBestVoPageResult.setCounts(pageResult.getCounts());
//        todayBestVoPageResult.setPagesize(pageResult.getPagesize());
//        todayBestVoPageResult.setPages(pageResult.getPages());
//        todayBestVoPageResult.setPage(pageResult.getPage());
//        todayBestVoPageResult.setItems(todayBestVos);

        return pageResult;
    }

    //构造默认数据
    private List<RecommendUser> defaultRecommend() {
        String ids = "2,3,4,5,6,7,8,9,10,11";
        List<RecommendUser> records = new ArrayList<>();
        for (String id : ids.split(",")) {
            RecommendUser recommendUser = new RecommendUser();
            recommendUser.setUserId(Long.valueOf(id));
            recommendUser.setScore(RandomUtils.nextDouble(70, 98));
            records.add(recommendUser);
        }
        return records;
    }
}