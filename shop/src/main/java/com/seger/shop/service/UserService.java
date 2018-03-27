package com.seger.shop.service;

import com.seger.shop.dataobject.UserInfo;

/**
 * 使用者服务接口
 *
 * @author: seger.lin
 */

public interface UserService {

    UserInfo findUserInfoByUsernameAndPassword(String username, String password);

    /*添加用户*/
    UserInfo save(UserInfo userInfo);

    UserInfo findUserInfoByUsername(String username);
}
