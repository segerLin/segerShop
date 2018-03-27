package com.seger.shop.repository;

import com.seger.shop.dataobject.ProductInfo;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductInfoRepositoryTest {

    @Autowired
    private ProductInfoRepository repository;

    @Test
    public void saveTest(){
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductId("123");
        productInfo.setProductName("单元测试");
        productInfo.setProductPrice(new BigDecimal(3.2));
        productInfo.setProductStock(200);
        productInfo.setProductDescription("这是一个单元测试");
        productInfo.setProductImg("http://1234.jpg");
        productInfo.setProductStatus(0);
        productInfo.setCategoryType(100);

        ProductInfo result = repository.save(productInfo);
        TestCase.assertNotNull(result);
    }

    @Test
    public void findByProductStatus() {
        List<ProductInfo> productInfoList = repository.findByProductStatus(0);
        TestCase.assertNotSame(0, productInfoList.size());
    }
}