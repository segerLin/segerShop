package com.seger.shop.service.impl;

import com.seger.shop.dataobject.ProductCategory;
import junit.framework.TestCase;
import  org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryServiceImplTest {

    @Autowired
    private CategoryServiceImpl categoryService;

    @Test
    public void findOne() {
        ProductCategory productCategory = categoryService.findOne(1);
        TestCase.assertEquals(new Integer(1), productCategory.getCategoryId());
    }

    @Test
    public void findAll() {
        List<ProductCategory> productCategories = categoryService.findAll();
        TestCase.assertNotSame(0,productCategories.size());
    }

    @Test
    public void findByCategoryTypeIn() {
        List<ProductCategory> productCategories = categoryService.findByCategoryTypeIn(Arrays.asList(1,2,3));
        TestCase.assertNotNull(productCategories);
    }

    @Test
    public void save() {
        ProductCategory productCategory = new ProductCategory("类别测试", 100);
        ProductCategory result = categoryService.save(productCategory);
        TestCase.assertNotNull(result);
    }
}