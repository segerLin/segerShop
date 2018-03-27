package com.seger.shop.controller;

import com.seger.shop.dataobject.ProductCategory;
import com.seger.shop.dataobject.ProductInfo;
import com.seger.shop.service.CategoryService;
import com.seger.shop.service.ProductService;
import com.seger.shop.utils.ResultVOUtil;
import com.seger.shop.vo.ProductInfoVO;
import com.seger.shop.vo.ProductVO;
import com.seger.shop.vo.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 买家商品的controller
 *
 * @author: seger.lin
 */

@RestController
@RequestMapping("/buyer/product")
@CrossOrigin
public class BuyerProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;


    /*
    * 列出所有的商品---附带类型信息*/
    @GetMapping("/list")
    @CrossOrigin
    public ResultVO list(){
        /*1、查询全部列表主要逻辑是通过查询所有上架的商品*/
        List<ProductInfo> productInfoList = productService.findUpAll();

        /*2、获取全部的产品类型*/
        /* 在此处用了lambda的语法*/
        List<Integer> categoryTypeList = productInfoList.stream()
                .map(e -> e.getCategoryType())
                .collect(Collectors.toList());
        List<ProductCategory> productCategoryList = categoryService.findByCategoryTypeIn(categoryTypeList);


        HashMap<Integer, Integer> assist = new HashMap<Integer, Integer>();
        int index = 0;

        /* 对数据进行拼装*/
        List<ProductVO> productVOList=  new LinkedList<ProductVO>();
        for(ProductCategory productCategory: productCategoryList){
            ProductVO productVO = new ProductVO();
            productVO.setCategoryType(productCategory.getCategoryType());
            productVO.setCategoryName(productCategory.getCategoryName());
            productVO.setProductInfoVOList(new ArrayList<ProductInfoVO>());
            assist.put(productCategory.getCategoryType(), index++);
            productVOList.add(productVO);
        }

        for(ProductInfo productInfo: productInfoList){
            List<ProductInfoVO> productInfoVOList = productVOList.get(assist.get(productInfo.getCategoryType())).getProductInfoVOList();
            ProductInfoVO productInfoVO = new ProductInfoVO();
            BeanUtils.copyProperties(productInfo, productInfoVO);
            productInfoVOList.add(productInfoVO);
        }

        /*对结果进行封装*/
        return ResultVOUtil.success(productVOList);
    }

    /* 列出所有商品*/
    //http://localhost:8080/seger/shop/buyer/product/list/?page=10&sort=&priceGt=&priceLte=
    @GetMapping("/goods")
    @CrossOrigin
    public ResultVO goods(@RequestParam(value = "page", defaultValue = "1") Integer page,
                          @RequestParam(value = "sort", defaultValue = "1") Integer sort,
                          @RequestParam(value = "priceGt", defaultValue = "0.0") BigDecimal priceGt,
                          @RequestParam(value = "priceLte", defaultValue = "10000000000000") BigDecimal priceLte){
        /*1、查询全部列表主要逻辑是通过查询所有上架的商品*/
        PageRequest request = new PageRequest(page - 1, 10);
        Page<ProductInfo> productInfoPage = productService.findUpAll(request);
        List<ProductInfoVO> productInfoVOList = new ArrayList<ProductInfoVO>();
        for(ProductInfo productInfo: productInfoPage){
            ProductInfoVO productInfoVO = new ProductInfoVO();
            BeanUtils.copyProperties(productInfo, productInfoVO);
            productInfoVOList.add(productInfoVO);
        }

        return ResultVOUtil.success(productInfoVOList);
    }

    @GetMapping("/goodsDetails")
    @CrossOrigin
    public ResultVO goods(@RequestParam(value = "productId") String productId){
        ProductInfo productInfo = productService.findOne(productId);
        ProductInfoVO productInfoVO = new ProductInfoVO();
        BeanUtils.copyProperties(productInfo, productInfoVO);
        return ResultVOUtil.success(productInfoVO);
    }

}
