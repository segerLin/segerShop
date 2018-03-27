package com.seger.shop.service;

import com.seger.shop.dto.OrderDTO;

/**
 * 买家服务接口
 *
 * @author: seger.lin
 */

public interface BuyerService {

    //查询一个订单
    OrderDTO findOrderOne(String openid, String orderId);

    //取消订单
    OrderDTO cancelOrder(String openid, String orderId);
}