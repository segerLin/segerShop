package com.seger.shop.repository;

import com.seger.shop.dataobject.ProductCategory;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 进行单元测试
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductCategoryRepositoryTest {
    @Autowired
    private ProductCategoryRepository repository;

    @Test
    public void findOneTest(){
        ProductCategory productCategory = repository.findById(1).get();
        TestCase.assertNotNull(productCategory);
    }

    @Test
    public void saveTest(){
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName("测试");
        productCategory.setCategoryType(1);
        ProductCategory result = repository.save(productCategory);
        TestCase.assertNotNull(result);
    }

    @Test
    public void modifyTest(){
        ProductCategory productCategory = repository.findById(1).get();
        productCategory.setCategoryType(2);
        productCategory.setCategoryName("电子产品");
        ProductCategory result = repository.save(productCategory);
        TestCase.assertNotNull(result);
    }

    @Test
    public void findByCategoryTypeInTest(){
        List<Integer> list = Arrays.asList(3);
        List<ProductCategory> result = repository.findByCategoryTypeIn(list);
        TestCase.assertNotSame(0,result.size());
    }

}
