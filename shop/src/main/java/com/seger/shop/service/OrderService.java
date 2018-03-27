package com.seger.shop.service;

import com.seger.shop.dataobject.OrderDetail;
import com.seger.shop.dataobject.OrderMaster;
import com.seger.shop.dto.OrderDTO;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 订单操作相关接口
 *
 * @author: seger.lin
 */

public interface OrderService {

    /** 创建订单. */
    OrderDTO create(OrderDTO orderDTO, Integer orderStatus);

    /** 查询单个订单. */
    OrderDTO findOne(String orderId);

    /** 查询订单列表. */
    Page<OrderDTO> findList(String buyerOpenid, Pageable pageable);

    /** 取消订单. */
    OrderDTO cancel(OrderDTO orderDTO);

    /** 完结订单. */
    OrderDTO finish(OrderDTO orderDTO);

    /** 支付订单. */
    OrderDTO paid(OrderDTO orderDTO);

    /** 查询订单列表. */
    Page<OrderDTO> findList(Pageable pageable);

    /** 获得购物车 */
    OrderDTO getCart(String buyerOpenid);

    /** 购物车添加商品. */
    OrderDTO addCart(String productId, String userId, Integer num);

    OrderDTO delCart(String productId, String userId);

    /** 购物车下单. */
    OrderDTO finishCart(OrderDTO orderDTO);

    /** 清空购物车. */
    OrderDTO clearCart(OrderDTO orderDTO);

    /** 编辑购物车 */
    OrderDTO editCart(String productId, String userId, Integer num);
}
