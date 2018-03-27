package com.seger.shop.enums;

import lombok.Getter;

@Getter
public enum UserTypeEnum implements CodeEnum{
    BUYER(0, "买家"),
    SELLER(1, "卖家"),
    ;

    private Integer code;

    private String message;

    UserTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
