package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Settings;
import com.tanhua.domain.vo.PublishVo;


public interface MovementsApi {

    /**
    * @Desc: 发布动态
    * @Param: [publishVo]
    * @return: void
    */
    void createPublish(PublishVo publishVo);
}
