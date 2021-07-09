package com.tanhua.dubbo.api;

import com.tanhua.domain.db.Settings;


public interface MessageApi {

    void addFriends(Long userId, Long friendUserId);
}
