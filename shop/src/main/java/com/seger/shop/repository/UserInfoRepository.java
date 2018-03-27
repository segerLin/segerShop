package com.seger.shop.repository;

import com.seger.shop.dataobject.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户信息仓库
 *
 * @author: seger.lin
 */

public interface UserInfoRepository extends JpaRepository<UserInfo, String>{
    UserInfo findUserInfoByUsernameAndPassword(String username, String password);

    UserInfo findUserInfoByUsername(String username);
}

