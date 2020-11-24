package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.AttrVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品属性
 *
 * @author pakchoi
 * @email lxf@atguigu.com
 * @date 2020-09-10 22:34:04
 */
public interface AttrService extends IService<AttrEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryAttrsByCid(QueryCondition condition, Long cid222, Integer type);

    void saveAttr(AttrVO attrVO);
}

