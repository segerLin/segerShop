package com.seger.shop.form;

import lombok.Data;

import java.util.List;

/**
 * @author: seger.lin
 */



@Data
public class CartBatchForm {

    private String userId;

    private List<CartItemForm> productMsg;

}
