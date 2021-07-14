package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Settings;


public interface BaiduApi {

    // 上报地理位置
    void reportLocation(Double latitude, Double longitude, String addrStr, Long userId);
}
