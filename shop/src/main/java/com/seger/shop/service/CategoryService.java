package com.seger.shop.service;

import com.seger.shop.dataobject.ProductCategory;

import java.util.List;

/**
 * 类别
 * @author: seger.lin
 */

public interface CategoryService {
    ProductCategory findOne(Integer categoryId);

    /**这个方法主要用于后台查询*/
    List<ProductCategory> findAll();

    List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryList);

    /**用于插入类别数据*/
    ProductCategory save(ProductCategory productCategory);
}
