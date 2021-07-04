package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.Announcement;
import com.tanhua.domain.db.Settings;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.vo.AnnouncementVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.mapper.AnnoMapper;
import com.tanhua.dubbo.mapper.UserSettingMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AnnoApiImpl implements AnnoApi {

    @Autowired
    private AnnoMapper annoMapper;

    /**
    * @Desc: 展示公告
    * @Param: [page, pagesize]
    * @return: com.tanhua.domain.vo.PageResult<com.tanhua.domain.db.Announcement>
    */
    @Override
    public PageResult<Announcement> getAnnoList(int page, int pagesize) {

        Page<Announcement> pages = new Page<>(page,pagesize);
        QueryWrapper<Announcement> annoQueryWrapper = new QueryWrapper<>();

        IPage<Announcement> pageInfo = annoMapper.selectPage(pages, annoQueryWrapper);

        PageResult<Announcement> pageResult = new PageResult<>(pageInfo.getTotal(), pageInfo.getSize(), pageInfo.getPages(), pageInfo.getCurrent(), pageInfo.getRecords());

        return pageResult;
    }
}
