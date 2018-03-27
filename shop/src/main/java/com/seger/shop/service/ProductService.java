package com.seger.shop.service;

import com.seger.shop.dataobject.ProductInfo;
import com.seger.shop.dto.CartDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    /*查询一件商品*/
    ProductInfo findOne(String prodcutId);

    /*查询所有在架的商品*/
    List<ProductInfo> findUpAll();

    /*主要用于分页的查找*/
    Page<ProductInfo> findAll(Pageable pageable);

    /*添加商品*/
    ProductInfo save(ProductInfo productInfo);

    /*加库存*/
    void increaseStock(List<CartDTO> cartDTOList);

    /*减库存*/
    void decreaseStock(List<CartDTO> cartDTOList);

    /*上架*/
    ProductInfo onSale(String productId);

    /*下架*/
    ProductInfo offSale(String productId);

    /* 分页查找上架商品 */
    Page<ProductInfo> findUpAll(Pageable pageable);

}
