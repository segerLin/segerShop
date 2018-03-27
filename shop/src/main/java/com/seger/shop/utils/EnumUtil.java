package com.seger.shop.utils;

import com.seger.shop.enums.CodeEnum;

/**
 * 枚举类型工具类
 *
 * @author: seger.lin
 */

public class EnumUtil {

    /*运用泛型和反射机制，获取枚举类型code对应的数据字段信息，方便前端，减少重复性代码*/
    public static <T extends CodeEnum> T getByCode(Integer code, Class<T> enumClass) {
        for (T each: enumClass.getEnumConstants()) {
            if (code.equals(each.getCode())) {
                return each;
            }
        }
        return null;
    }
}
