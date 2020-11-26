package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * @author pakchoi
 * @create 2020-11-23-23:07
 * 保存spu提供的信息一个巨大对象=SpuInfo+3个字段
 */

@Data
public class SpuInfoVO extends SpuInfoEntity {

    private List<String> spuImages;//spu的图像就是商品介绍的里的大图片。

    private List<BaseAttrVO> baseAttrs;//product_attr_value

    private  List<SkuInfoVO> skus;  //sku_info+促销信息

}
