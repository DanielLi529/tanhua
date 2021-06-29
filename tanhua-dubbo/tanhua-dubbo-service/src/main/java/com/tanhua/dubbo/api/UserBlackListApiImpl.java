package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.BlackList;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.mapper.UserBlackListMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserBlackListApiImpl implements UserBlackListApi {

    @Autowired
    private UserBlackListMapper userBlackListMapper;


    /**
     * 展示用户黑名单
     *
     * @param userId
     */
    @Override
    public PageResult<UserInfo> getBlackList(int page, int pagesize, Long userId) {
        Page pageRequest = new Page(page, pagesize);

        IPage<UserInfo> pageInfo = userBlackListMapper.findBlackList(pageRequest, userId);

        PageResult<UserInfo> pageResult = new PageResult<>(pageInfo.getTotal(), pageInfo.getSize(), pageInfo.getPages(), pageInfo.getCurrent(), pageInfo.getRecords());

        return pageResult;
    }

    /**
     * @Desc: 移除黑名单
     * @Param: [deleteUserId, userId]
     * @return: void
     */
    @Override
    public void deleteBlackList(long deleteUserId, Long userId) {
        // 构建条件
        UpdateWrapper<BlackList> blackListUpdateWrapper = new UpdateWrapper<>();
        blackListUpdateWrapper.eq("user_id", userId).eq("black_user_id", deleteUserId);

        userBlackListMapper.delete(blackListUpdateWrapper);
    }
}
