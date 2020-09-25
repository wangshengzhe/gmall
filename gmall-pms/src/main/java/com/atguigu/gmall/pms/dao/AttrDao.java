package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品属性
 * 
 * @author pakchoi
 * @email lxf@atguigu.com
 * @date 2020-09-10 22:34:04
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
	
}
