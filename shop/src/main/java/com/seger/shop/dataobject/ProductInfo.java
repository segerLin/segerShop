package com.seger.shop.dataobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seger.shop.enums.ProductStatusEnum;
import com.seger.shop.utils.EnumUtil;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@DynamicUpdate
public class ProductInfo {

    @Id
    private String productId;

    /** 商品名字 */
    private String productName;

    /** 商品单价 */
    private BigDecimal productPrice;

    /** 商品库存 */
    private Integer productStock;

    /** 描述 */
    private String productDescription;

    /** 商品图片*/
    private String productImg;

    /** 商品状态--- 0正常1下架*/
    private Integer productStatus;

    /** 类目编号 */
    private Integer categoryType;

    /** 商品内容分 */
    private String productContent;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    @JsonIgnore
    public ProductStatusEnum getProductStatusEnum() {
        return EnumUtil.getByCode(productStatus, ProductStatusEnum.class);
    }

}
