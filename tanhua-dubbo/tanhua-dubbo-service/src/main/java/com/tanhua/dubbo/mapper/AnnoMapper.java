package com.tanhua.dubbo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.domain.db.Announcement;
import org.apache.ibatis.annotations.Select;

public interface AnnoMapper extends BaseMapper<Announcement> {

    IPage<Announcement> getAnnoList(Page pageRequest);
}
