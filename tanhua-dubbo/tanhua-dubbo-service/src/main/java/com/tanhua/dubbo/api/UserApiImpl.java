package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tanhua.domain.db.User;
import com.tanhua.dubbo.mapper.UserMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Service
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;

    /**
     * 添加用户
     * @param user
     * @return
     */
    @Override
    public Long save(User user) {
//        user.setCreated(new Date());
//        user.setUpdated(new Date());
        userMapper.insert(user);
        return user.getId();
    }

    /**
     * 通过手机号码查询
     * @param mobile
     * @return
     */
    @Override
    public User findByMobile(String mobile) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile",mobile);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * @Desc: 更新手机号码
     * @Param: []
     * @return: org.springframework.http.ResponseEntity
     */
    @Override
    public void updateUserInfo(User user, Long userId) {
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.eq("id",userId);
        userMapper.update(user, userUpdateWrapper);
    }

    /**
     * @Desc: 通过id获取user对象
     * @Param: [userId]
     * @return: com.tanhua.domain.db.User
     */
    @Override
    public User findUserById(Long userId) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id",userId);
        return userMapper.selectOne(userQueryWrapper);
    }
}
