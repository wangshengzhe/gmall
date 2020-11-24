package com.atguigu.sms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.sms.vo.SkuSaleVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author pakchoi
 * @create 2020-11-24-21:39
 */
public interface GmallSmsApi {
    @PostMapping("sms/skubounds/sku/sale/save")//远程调用要补齐一级路径
    public Resp<Object> saveSale(@RequestBody SkuSaleVO skuSaleVO);
}
