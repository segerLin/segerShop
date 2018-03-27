package com.seger.shop.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seger.shop.dataobject.ProductInfo;
import lombok.Data;

import java.util.List;

/**
 * 商品的类别信息视图
 *
 * @author: seger.lin
 */

@Data
public class ProductVO {
    /**
     * 主要通过json进行序列化
     */

    /* 返回产品类别名称*/
    @JsonProperty("name")
    private String categoryName;

    /* 返回产品类别信息*/
    @JsonProperty("type")
    private Integer categoryType;

    /* 返回具体的产品信息*/
    @JsonProperty("productInfo")
    private List<ProductInfoVO> productInfoVOList;
}
