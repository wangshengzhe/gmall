package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author pakchoi
 * @create 2020-11-24-1:22
 * sku_info+促销信息字段
 */
@Data
public class SkuInfoVO extends SkuInfoEntity {

    // sku图片
    private List<String> images;

    // 积分营销相关字段
    private BigDecimal growBounds;  //DECIMAL=BigDecimal
    private BigDecimal buyBounds;
    private List<Integer> work;

    // 打折相关的字段
    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;//注意和json一致

    // 满减相关的字段
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;

    // 销售属性及值
    private List<SkuSaleAttrValueEntity> saleAttrs;

}
