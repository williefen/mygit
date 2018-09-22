package com.pinyougou.pay.service;

import java.util.Map;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/22
 * @Param $param
 **/

public interface WeixinPayService {
    /**
     *  根据支付日志 id 到微信支付创建支付订单并返回支付二维码地址等信息
     * @param outTradeNo
     * @param totalFee
     * @return
     */
    Map<String,String> crateNative(String outTradeNo,String totalFee);


    /**
     * 根据商户订单号查询该订单在微信支付系统的支付状态
     * @param outTradeNo 商户订单号
     * @return 支付信息
     */
    Map<String, String> queryPayStatus(String outTradeNo);

}
