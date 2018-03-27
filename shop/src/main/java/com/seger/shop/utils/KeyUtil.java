package com.seger.shop.utils;

import java.util.Random;

/**
 * 产生唯一的key--工具类
 * 格式为： 时间 + 随机数
 * @author: seger.lin
 */

public class KeyUtil {

    /*为了防止并发导致的key值不唯一，这里使用synchronized*/
    public static synchronized String genUniqueKey() {
        Random random = new Random();
        Integer number = random.nextInt(900000) + 100000;

        return System.currentTimeMillis() + String.valueOf(number);
    }
}
