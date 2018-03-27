package com.seger.shop.vo;

import lombok.Data;

/**
 * 请求返回对象的视图（最外层）
 *
 * @author: seger.lin
 */

@Data
public class ResultVO<T> {
    /* 状态码 */
    private  Integer code;

    /* 返回提示信息*/
    private  String msg;

    /* 返回数据 */
    private T data;

}
