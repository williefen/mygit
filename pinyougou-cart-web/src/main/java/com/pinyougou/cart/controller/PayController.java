package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/22
 * @Param $param
 **/
@RequestMapping("/pay")
@RestController
public class PayController {

    @Reference
    private OrderService orderService;
    @Reference(timeout = 3000)
    private WeixinPayService weixinPayService;

    /**
     * 调用支付系统的统一下单地址返回支付二维码等信息
     *
     * @param outTradeNo
     * @return
     */
    @GetMapping("/createNative")
    private Map<String, String> createNative(String outTradeNo) {
        try {
            //1、查询出本次要支付总金额
            TbPayLog payLog = orderService.findPayLogById(outTradeNo);

            if (payLog != null) {
                //本次要支付的总金额
                String totalFee = payLog.getTotalFee().toString();

                //2、 调用支付系统业务对象的生成二维码的方法返回一些信息
                return weixinPayService.crateNative(outTradeNo, totalFee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    /**
     * 根据支付日志id查询支付状态
     * @param outTradeNo
     * @return 操作结果
     */
    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo){
        Result result = Result.fail("查询支付状态失败");
        //如果3分钟未支付则提示支付超时并重新自动生成新的支付二维码
        int count=0;
        try {
            while(true){
                // 1
                Map<String,String> resultMap=weixinPayService.queryPayStatus(outTradeNo);
               if (resultMap == null){
                   break;
               }
               if ("SUCCESS".equals(resultMap.get("trade_state"))){

                   orderService.updateOrderStatus(outTradeNo,resultMap.get("transaction_id"));

                   result=Result.ok("支付成功OK");
                   return  result;
               }
               Thread.sleep(3000);
               count++;
               if (count > 60){
                   result =Result.fail("支付超时");
                   break;
               }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return  result;
    }
}
