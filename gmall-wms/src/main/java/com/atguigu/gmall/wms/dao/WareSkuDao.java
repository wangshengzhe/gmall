package com.atguigu.gmall.wms.dao;

import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author pakchoi
 * @email pakhoi@atguigu.com
 * @date 2020-11-23 19:32:12
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
