package com.seger.shop.repository;

import com.seger.shop.dataobject.ProductInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductInfoRepository extends JpaRepository<ProductInfo, String>{
    List<ProductInfo> findByProductStatus(Integer productStatus);

    Page<ProductInfo> findByProductStatus(Integer productStatus, Pageable pageable);
}
