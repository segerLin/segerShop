package com.seger.shop.controller;

import com.seger.shop.converter.OrderForm2OrderDTOConverter;
import com.seger.shop.dto.OrderDTO;
import com.seger.shop.enums.OrderStatusEnum;
import com.seger.shop.enums.ResultEnum;
import com.seger.shop.exception.SellException;
import com.seger.shop.form.OrderForm;
import com.seger.shop.service.BuyerService;
import com.seger.shop.service.OrderService;
import com.seger.shop.utils.ResultVOUtil;
import com.seger.shop.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 买家订单控制器
 *
 * @author: seger.lin
 */
@RestController
@RequestMapping("/buyer/order")
@Slf4j
public class BuyerOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BuyerService buyerService;

    //创建订单
    @PostMapping("/create")
    public ResultVO<Map<String, String>> create(@Valid OrderForm orderForm,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, orderForm={}", orderForm);
            throw new SellException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);
        if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())) {
            log.error("【创建订单】购物车不能为空");
            throw new SellException(ResultEnum.CART_EMPTY);
        }

        OrderDTO createResult = orderService.create(orderDTO, OrderStatusEnum.NEW.getCode());

        Map<String, String> map = new HashMap<>();
        map.put("orderId", createResult.getOrderId());

        return ResultVOUtil.success(map);
    }

    //订单列表
    @GetMapping("/list")
    @CrossOrigin
    public ResultVO<List<OrderDTO>> list(@RequestParam("openid") String openid,
                                         @RequestParam(value = "page", defaultValue = "0") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (StringUtils.isEmpty(openid)) {
            log.error("【查询订单列表】openid为空");
            throw new SellException(ResultEnum.PARAM_ERROR);
        }

        PageRequest request = new PageRequest(page, size);
        Page<OrderDTO> orderDTOPage = orderService.findList(openid, request);
        List<OrderDTO> orderDTOList = new ArrayList<OrderDTO>();
        for(OrderDTO orderDTO : orderDTOPage){
            if(orderDTO.getOrderStatus().equals(OrderStatusEnum.CART.getCode())){
                continue;
            }
            orderDTOList.add(orderDTO);
        }
        return ResultVOUtil.success(orderDTOList);
    }


    //订单详情
    @GetMapping("/detail")
    @CrossOrigin
    public ResultVO<OrderDTO> detail(@RequestParam("userId") String userId,
                                     @RequestParam("orderId") String orderId) {
        OrderDTO orderDTO = buyerService.findOrderOne(userId, orderId);
        return ResultVOUtil.success(orderDTO.getOrderDetailList());
    }

    //取消订单
    @PostMapping("/cancel")
    public ResultVO cancel(@RequestParam("openid") String openid,
                           @RequestParam("orderId") String orderId) {
        buyerService.cancelOrder(openid, orderId);
        return ResultVOUtil.success();
    }

    //完成订单
    @GetMapping("/finish")
    public ResultVO finish(@RequestParam("openid") String openid,
                           @RequestParam("orderId") String orderId){
        OrderDTO orderDTO = buyerService.findOrderOne(openid,orderId);
        orderService.paid(orderDTO);
        orderService.finish(orderDTO);
        return ResultVOUtil.success();
    }

}
