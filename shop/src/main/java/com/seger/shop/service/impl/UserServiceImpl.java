package com.seger.shop.service.impl;

import com.seger.shop.dataobject.UserInfo;
import com.seger.shop.repository.UserInfoRepository;
import com.seger.shop.service.UserService;
import com.seger.shop.utils.KeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: seger.lin
 */

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserInfoRepository repository;

    @Override
    public UserInfo findUserInfoByUsernameAndPassword(String username, String password) {
        UserInfo info = repository.findUserInfoByUsernameAndPassword(username, password);
        return info;
    }

    @Override
    public UserInfo save(UserInfo userInfo) {
        String userId = KeyUtil.genUniqueKey();
        userInfo.setUserId(userId);
        return repository.save(userInfo);
    }

    @Override
    public UserInfo findUserInfoByUsername(String username) {
        return repository.findUserInfoByUsername(username);
    }
}
