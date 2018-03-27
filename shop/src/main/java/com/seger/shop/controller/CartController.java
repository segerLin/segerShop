package com.seger.shop.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seger.shop.constant.CookieConstant;
import com.seger.shop.dataobject.OrderDetail;
import com.seger.shop.dataobject.OrderMaster;
import com.seger.shop.dataobject.ProductInfo;
import com.seger.shop.dto.OrderDTO;
import com.seger.shop.form.CartBatchForm;
import com.seger.shop.form.CartItemForm;
import com.seger.shop.repository.OrderDetailRepository;
import com.seger.shop.service.OrderService;
import com.seger.shop.service.ProductService;
import com.seger.shop.utils.CookieUtil;
import com.seger.shop.utils.KeyUtil;
import com.seger.shop.utils.ResultVOUtil;
import com.seger.shop.vo.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车
 *
 * @author: seger.lin
 */
@RestController
@RequestMapping("/buyer/cart")
public class CartController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @GetMapping("/list")
    @CrossOrigin
    public ResultVO list(@RequestParam("userId") String userId){
        OrderDTO orderDTO = orderService.getCart(userId);
        return ResultVOUtil.success(orderDTO.getOrderDetailList());
    }

    @GetMapping("/addCart")
    @CrossOrigin
    public ResultVO addCart(@RequestParam("userId") String userid,
                            @RequestParam("productId") String productid,
                            @RequestParam("productNum") Integer productNum){
        orderService.addCart(productid,userid, productNum);
        return ResultVOUtil.success();
    }

    @GetMapping("/del")
    @CrossOrigin
    public ResultVO delCart(@RequestParam("productId") String productId,
                            @RequestParam("userId") String userId){
        orderService.delCart(productId, userId);
        return ResultVOUtil.success();
    }


    @GetMapping("/finish")
    @CrossOrigin
    public ResultVO finish(@RequestParam("userId") String userId,
                           @RequestParam("userName") String userName,
                           @RequestParam("userPhone") String userPhone,
                           @RequestParam("address") String address){
        OrderDTO orderDTO = orderService.getCart(userId);
        orderDTO.setBuyerPhone(userPhone);
        orderDTO.setBuyerAddress(address);
        orderDTO.setBuyerName(userName);
        orderService.finishCart(orderDTO);
        return ResultVOUtil.success();
    }

    @GetMapping("/clear")
    @CrossOrigin
    public ResultVO clear(@RequestParam("userId") String userId){
        OrderDTO orderDTO = orderService.getCart(userId);
        orderService.clearCart(orderDTO);
        return ResultVOUtil.success();
    }

    @PostMapping("/addCartBatch")
    @CrossOrigin
    public ResultVO addCartBatch(@RequestBody CartBatchForm cartBatchForm){

        for(CartItemForm cartItemForm : cartBatchForm.getProductMsg()){
            orderService.addCart(cartItemForm.getProductId(),cartBatchForm.getUserId(),cartItemForm.getProductNum());
        }
        return ResultVOUtil.success(cartBatchForm.getUserId());
    }

    @GetMapping("/edit")
    @CrossOrigin
    public ResultVO edit(@RequestParam("userId") String userid,
                         @RequestParam("productId") String productid,
                         @RequestParam("productNum") Integer productNum){
        orderService.editCart(productid, userid, productNum);
        return ResultVOUtil.success();
    }

}
