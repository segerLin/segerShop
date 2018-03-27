package com.seger.shop.dataobject;

import com.seger.shop.enums.UserTypeEnum;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 使用者信息
 *
 * @author: seger.lin
 */

@Entity
@Data
public class UserInfo {

    @Id
    private String userId;

    private String username;

    private String password;

    /** 用户类型, 默认为0 - 买家. */
    private Integer userType = UserTypeEnum.BUYER.getCode();

}
