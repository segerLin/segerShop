package com.seger.shop.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * json序列化
 *
 * @author: seger.lin
 */

public class JsonUtil {

    public static String toJson(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(object);
    }
}