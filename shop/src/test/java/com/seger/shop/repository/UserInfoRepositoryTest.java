package com.seger.shop.repository;

import com.seger.shop.dataobject.UserInfo;
import com.seger.shop.utils.KeyUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserInfoRepositoryTest {

    @Autowired
    private UserInfoRepository repository;

    @Test
    public void save(){
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(KeyUtil.genUniqueKey());
        userInfo.setUsername("seller");
        userInfo.setPassword("relles");
        userInfo.setUserType(1);

        UserInfo result = repository.save(userInfo);
    }

    @Test
    public void findStatus(){
        UserInfo userInfo = repository.findById("1521706237436277146").get();
        Assert.assertEquals((Integer)0, userInfo.getUserType());
    }

}