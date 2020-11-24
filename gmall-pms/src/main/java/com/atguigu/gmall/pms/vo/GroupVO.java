package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @author pakchoi
 * @create 2020-11-22-21:36
 *
 * 当页面对象和数据库对象不一致时，我们自定义一个vo对象
 */


@Data
public class GroupVO extends AttrGroupEntity{

   private List<AttrEntity> attrEntities;//属性名不能自定义，因为json中需要。
   private List<AttrAttrgroupRelationEntity> relations;

}
