package com.tanhua.dubbo.api;

import com.tanhua.domain.db.UserInfo;
import com.tanhua.dubbo.mapper.UserInfoMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserInfoApiImpl implements UserInfoApi {

    @Autowired
    private UserInfoMapper userInfoMapper;


    /**
     * 保存用户基本信息
     * @param userInfo
     */
    @Override
    public void saveUserInfo(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    /**
     * 通过id更新用户基本信息
     * @param userInfo
     */
    @Override
    public void updateUserInfo(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    /**
     * 通过id查找用户基本信息
     * @param id
     */
    @Override
    public UserInfo getUserInfo(Long id) {
        return userInfoMapper.selectById(id);
    }

    /**
    * @Desc: 更新用户头像
    * @Param: [userInfo]
    * @return: void
    */
    @Override
    public void updateUserHead(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }
}
