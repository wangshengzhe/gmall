package com.atguigu.sms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author pakchoi
 * @create 2020-11-24-19:41
 */
@Data
public class SkuSaleVO {

    private Long skuId;

    // 积分营销相关字段
    private BigDecimal growBounds;  //DECIMAL=BigDecimal
    private BigDecimal buyBounds;
    private List<Integer> work;

    // 打折相关的字段
    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;//注意和json一致，是否叠加其他优惠[0-不可叠加，1-可叠加]

    // 满减相关的字段
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;
}
