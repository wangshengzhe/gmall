package com.atguigu.gmall.pms.feign;

        import com.atguigu.sms.api.GmallSmsApi;
        import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author pakchoi
 * @create 2020-11-24-20:41
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {


}
