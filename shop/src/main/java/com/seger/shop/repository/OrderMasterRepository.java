package com.seger.shop.repository;

import com.seger.shop.dataobject.OrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 订单主表仓库
 *
 * @author: seger.lin
 */

public interface OrderMasterRepository extends JpaRepository<OrderMaster, String> {

    Page<OrderMaster> findByBuyerOpenid(String buyerOpenid, Pageable pageable);

    OrderMaster findByOrderStatusAndBuyerOpenid(Integer status, String buyerOpenid);

    List<OrderMaster> findByBuyerOpenid(String buyerOpenid);


}
