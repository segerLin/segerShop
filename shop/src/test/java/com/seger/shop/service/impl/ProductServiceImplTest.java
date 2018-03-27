package com.seger.shop.service.impl;

import com.seger.shop.dataobject.ProductInfo;
import com.seger.shop.enums.ProductStatusEnum;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceImplTest {

    @Autowired
    private ProductServiceImpl productService;

    @Test
    public void findOne() {
        ProductInfo productInfo = productService.findOne("123");
        TestCase.assertEquals("123",  productInfo.getProductId());
    }

    @Test
    public void findUpAll() {
        List<ProductInfo> productInfoList = productService.findUpAll();
        TestCase.assertNotSame(0, productInfoList.size());
    }

    @Test
    public void finaAll() {
        PageRequest request = new PageRequest(0,2);
        Page<ProductInfo> productInfoPage = productService.findAll(request);
        TestCase.assertNotSame(0, productInfoPage.getTotalElements());
    }

    @Test
    public void save() {
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductId("1234");
        productInfo.setProductName("单元测试");
        productInfo.setProductPrice(new BigDecimal(3.2));
        productInfo.setProductStock(200);
        productInfo.setProductDescription("这是又一个单元测试");
        productInfo.setProductImg("http://1234.jpg");
        productInfo.setProductStatus(1);
        productInfo.setCategoryType(100);
        ProductInfo result = productService.save(productInfo);

        TestCase.assertNotNull(result);
    }

    @Test
    public void onSale() {
        ProductInfo result = productService.onSale("12345");
        TestCase.assertEquals(ProductStatusEnum.UP, result.getProductStatusEnum());
    }

    @Test
    public void offSale() {
        ProductInfo result = productService.offSale("12345");
        TestCase.assertEquals(ProductStatusEnum.DOWN, result.getProductStatusEnum());
    }
}