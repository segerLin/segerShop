package com.seger.shop.service.impl;

import com.seger.shop.converter.OrderMaster2OrderDTOConverter;
import com.seger.shop.dataobject.OrderDetail;
import com.seger.shop.dataobject.OrderMaster;
import com.seger.shop.dataobject.ProductInfo;
import com.seger.shop.dto.CartDTO;
import com.seger.shop.dto.OrderDTO;
import com.seger.shop.enums.OrderStatusEnum;
import com.seger.shop.enums.PayStatusEnum;
import com.seger.shop.enums.ResultEnum;
import com.seger.shop.exception.SellException;
import com.seger.shop.repository.OrderDetailRepository;
import com.seger.shop.repository.OrderMasterRepository;
import com.seger.shop.service.OrderService;
import com.seger.shop.service.ProductService;
import com.seger.shop.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Order;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单接口实例
 *
 * @author: seger.lin
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderMasterRepository orderMasterRepository;


    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO, Integer orderStatus) {

        String orderId = KeyUtil.genUniqueKey();
        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);

        //1. 查询商品（数量, 价格）
        for (OrderDetail orderDetail: orderDTO.getOrderDetailList()) {
            ProductInfo productInfo =  productService.findOne(orderDetail.getProductId());
            if (productInfo == null) {
                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }

            //2. 计算订单总价
            orderAmount = productInfo.getProductPrice()
                    .multiply(new BigDecimal(orderDetail.getProductNum()))
                    .add(orderAmount);

            //订单详情入库
            orderDetail.setDetailId(KeyUtil.genUniqueKey());
            orderDetail.setOrderId(orderId);
            BeanUtils.copyProperties(productInfo, orderDetail);
            orderDetailRepository.save(orderDetail);
        }

        //3. 写入订单数据库（orderMaster和orderDetail）
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO, orderMaster);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(orderStatus);
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
        orderMasterRepository.save(orderMaster);

        //4. 扣库存
        List<CartDTO> cartDTOList = orderDTO.getOrderDetailList().stream().map(e ->
                new CartDTO(e.getProductId(), e.getProductNum())
        ).collect(Collectors.toList());
        productService.decreaseStock(cartDTOList);



        return orderDTO;
    }

    @Override
    public OrderDTO findOne(String orderId) {

        OrderMaster orderMaster = orderMasterRepository.findById(orderId).get();
        if (orderMaster == null) {
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);
        if (CollectionUtils.isEmpty(orderDetailList)) {
            throw new SellException(ResultEnum.ORDERDETAIL_NOT_EXIST);
        }

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderMaster, orderDTO);
        orderDTO.setOrderDetailList(orderDetailList);

        return orderDTO;
    }

    @Override
    public Page<OrderDTO> findList(String buyerOpenid, Pageable pageable) {
        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByBuyerOpenid(buyerOpenid, pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        return new PageImpl<OrderDTO>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }

    @Override
    @Transactional
    public OrderDTO cancel(OrderDTO orderDTO) {
        OrderMaster orderMaster = new OrderMaster();

        //判断订单状态
        if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("【取消订单】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        //修改订单状态
        orderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        BeanUtils.copyProperties(orderDTO, orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if (updateResult == null) {
            log.error("【取消订单】更新失败, orderMaster={}", orderMaster);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        //返回库存
        if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())) {
            log.error("【取消订单】订单中无商品详情, orderDTO={}", orderDTO);
            throw new SellException(ResultEnum.ORDER_DETAIL_EMPTY);
        }
        List<CartDTO> cartDTOList = orderDTO.getOrderDetailList().stream()
                .map(e -> new CartDTO(e.getProductId(), e.getProductNum()))
                .collect(Collectors.toList());
        productService.increaseStock(cartDTOList);

//        //如果已支付, 需要退款
//        if (orderDTO.getPayStatus().equals(PayStatusEnum.SUCCESS.getCode())) {
//
//        }

        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO finish(OrderDTO orderDTO) {
        //判断订单状态
        if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("【订单支付完成】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        //判断支付状态
        if (!orderDTO.getPayStatus().equals(PayStatusEnum.WAIT.getCode())) {
            log.error("【订单支付完成】订单支付状态不正确, orderDTO={}", orderDTO);
            throw new SellException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }

        //修改订单状态
        orderDTO.setOrderStatus(OrderStatusEnum.FINISHED.getCode());
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties(orderDTO, orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if (updateResult == null) {
            log.error("【完结订单】更新失败, orderMaster={}", orderMaster);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }


        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO paid(OrderDTO orderDTO) {
        //判断订单状态
        if (!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("【订单支付完成】订单状态不正确, orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        //判断支付状态
        if (!orderDTO.getPayStatus().equals(PayStatusEnum.WAIT.getCode())) {
            log.error("【订单支付完成】订单支付状态不正确, orderDTO={}", orderDTO);
            throw new SellException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }

        //修改支付状态
        orderDTO.setPayStatus(PayStatusEnum.SUCCESS.getCode());
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties(orderDTO, orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if (updateResult == null) {
            log.error("【订单支付完成】更新失败, orderMaster={}", orderMaster);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        return orderDTO;
    }

    @Override
    public Page<OrderDTO> findList(Pageable pageable) {
        Page<OrderMaster> orderMasterPage = orderMasterRepository.findAll(pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        return new PageImpl<>(orderDTOList, pageable, orderMasterPage.getTotalElements());
    }

    @Override
    public OrderDTO getCart(String buyerOpenid) {

        OrderMaster orderMaster = orderMasterRepository.findByOrderStatusAndBuyerOpenid(OrderStatusEnum.CART.getCode(), buyerOpenid);
        OrderDTO orderDTO = OrderMaster2OrderDTOConverter.convert(orderMaster);
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderDTO.getOrderId());
        orderDTO.setOrderDetailList(orderDetailList);
        return orderDTO;
    }

    @Override
    public OrderDTO addCart(String productId, String userId, Integer num) {
        /*定位到购物车*/
        OrderDTO orderDTO = getCart(userId);
        /* 向购物车添加相关产品*/
        List<OrderDetail> orderDetails = orderDTO.getOrderDetailList();
        boolean found = false;
        ProductInfo productInfo = productService.findOne(productId);
        for(OrderDetail orderDetail : orderDetails){
            /* 如果有，则数量加 */
            if(orderDetail.getProductId().equals(productId)){
                orderDetail.setProductNum(orderDetail.getProductNum() + num);
                orderDetailRepository.save(orderDetail);
                found = true;
                break;
            }
        }
        if(!found){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setDetailId(KeyUtil.genUniqueKey());
            orderDetail.setOrderId(orderDTO.getOrderId());
            orderDetail.setProductNum(num);
            BeanUtils.copyProperties(productInfo, orderDetail);
            orderDetailRepository.save(orderDetail);
        }
        /* 不需要减库存*/
        /* 更改购物车总价 */
        OrderMaster orderMaster = orderMasterRepository.findByOrderStatusAndBuyerOpenid(OrderStatusEnum.CART.getCode(), userId);
        orderMaster.setOrderAmount(orderMaster.getOrderAmount().add(productInfo.getProductPrice().multiply(new BigDecimal(num))));
        orderMasterRepository.save(orderMaster);
        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO delCart(String productId, String userId) {
        OrderDTO orderDTO = getCart(userId);
        OrderDetail orderDetail = orderDetailRepository.findOrderDetailByProductIdAndAndOrderId(productId, orderDTO.getOrderId());
        orderDetailRepository.deleteByOrderIdAndProductId(orderDTO.getOrderId(), productId);
        OrderMaster orderMaster = orderMasterRepository.findByOrderStatusAndBuyerOpenid(OrderStatusEnum.CART.getCode(), userId);
        orderMaster.setOrderAmount(orderMaster.getOrderAmount().subtract(orderDetail.getProductPrice().multiply(new BigDecimal(orderDetail.getProductNum()))));
        orderMasterRepository.save(orderMaster);
        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO finishCart(OrderDTO orderDTO) {

        /* 完成购物车
        * 1、创建一个订单
        * */
        String orderId = KeyUtil.genUniqueKey();
        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);
        for (OrderDetail orderDetail: orderDTO.getOrderDetailList()) {
            orderDetail.setOrderId(orderId);
            orderDetailRepository.save(orderDetail);
        }
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties(orderDTO, orderMaster);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.SUCCESS.getCode());
        orderMaster.setOrderId(orderId);
        orderMasterRepository.save(orderMaster);

        /*2、将订单包含内容清空*/
        orderDetailRepository.deleteByOrderId(orderDTO.getOrderId());
        /*购物车总额清零*/
        setOrderMount(orderDTO, new BigDecimal(0));
        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO clearCart(OrderDTO orderDTO) {
        orderDetailRepository.deleteByOrderId(orderDTO.getOrderId());
        setOrderMount(orderDTO, new BigDecimal(0));
        return orderDTO;
    }

    @Override
    public OrderDTO editCart(String productId, String userId, Integer now) {
        OrderDTO orderDTO = getCart(userId);
        Integer before = orderDetailRepository.findOrderDetailByProductIdAndAndOrderId(productId, orderDTO.getOrderId()).getProductNum();
        return addCart(productId, userId, now - before);
    }


    private void setOrderMount(OrderDTO orderDTO, BigDecimal bigDecimal){
        OrderMaster orderMaster = orderMasterRepository.findByOrderStatusAndBuyerOpenid(OrderStatusEnum.CART.getCode(), orderDTO.getBuyerOpenid());
        orderMaster.setOrderAmount(bigDecimal);
        orderMasterRepository.save(orderMaster);
    }

}
