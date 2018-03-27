package com.seger.shop.repository;

import com.seger.shop.dataobject.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 订单详细数据仓库
 *
 * @author: seger.lin
 */

public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

    List<OrderDetail> findByOrderId(String orderId);

    void deleteByOrderId(String orderId);

    void deleteByDetailId(String detailId);

    void deleteByOrderIdAndProductId(String orderId, String productId);

    OrderDetail findOrderDetailByProductIdAndAndOrderId(String productId, String orderId);
}
