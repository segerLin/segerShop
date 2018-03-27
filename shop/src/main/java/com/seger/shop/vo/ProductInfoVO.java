package com.seger.shop.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品具体信息视图对象
 *
 * @author: seger.lin
 */

@Data
public class ProductInfoVO {
    /**
     * 主要通过json进行序列化
     */

    /* 产品ID*/
    @JsonProperty("id")
    private  String productId;

    /*产品名称*/
    @JsonProperty("name")
    private String productName;

    /*产品价格*/
    @JsonProperty("price")
    private BigDecimal productPrice;

    /*产品描述*/
    @JsonProperty("description")
    private String productDescription;

    /*产品图片链接*/
    @JsonProperty("icon")
    private String productImg;

    /*商品内容*/
    @JsonProperty("content")
    private String productContent;
}
