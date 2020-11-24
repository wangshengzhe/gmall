package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author pakchoi
 * @create 2020-11-24-0:53
 *
 * 对应`pms_product_attr_value`但是返回的json中valueSelected是跟数据库的attr_value字段不一致。
 */
@Data
public class BaseAttrVO extends ProductAttrValueEntity {

    public void setValueSelected(List<String> selected){//没有声明valueSelected属性也可以的。

        if (CollectionUtils.isEmpty(selected)) {
            return ;
        }
        this.setAttrValue(StringUtils.join(selected, ","));//将list<string>转变为string
    }
}
