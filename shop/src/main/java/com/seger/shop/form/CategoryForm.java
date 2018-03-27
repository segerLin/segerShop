package com.seger.shop.form;

import lombok.Data;

/**
 * 商品类别表单
 *
 * @author: seger.lin
 */


@Data
public class CategoryForm {

    private Integer categoryId;

    /** 类目名字. */
    private String categoryName;

    /** 类目编号. */
    private Integer categoryType;
}
