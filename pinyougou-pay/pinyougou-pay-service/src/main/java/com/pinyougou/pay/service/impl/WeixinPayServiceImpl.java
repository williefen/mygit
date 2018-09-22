package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;


import java.util.HashMap;
import java.util.Map;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/22
 * @Param $param
 **/
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

   @Value("${appid}")
   private  String appid;
   @Value("${partner}")
   private  String partner;
   @Value("${partnerkey}")
   private  String partnerkey;
   @Value("${notifyurl}")
   private  String notifyurl;

   @Override
   public Map<String, String> crateNative(String outTradeNo, String totalFee) {
        Map<String, String> returnMap=new HashMap<>();
        try {
            Map <String,String> param =new HashMap<>();
            param.put("appid",appid);

            param.put("mch_id",partner);

            param.put("nonce_str", WXPayUtil.generateNonceStr());

           //param.put("sign","");

            param.put("body", "品优购");

            param.put("out_trade_no",outTradeNo);

            param.put("total_fee",totalFee);

            param.put("spbill_create_ip","127.0.0.1");

            param.put("notify_url", notifyurl);

            param.put("trade_type", "NATIVE");
           // 1.转换成为签名了的xml内容
          String signedXml=WXPayUtil.generateSignedXml(param,partnerkey);
          System.out.println("发送到微信支付系统 统一下单 的请求内容为："+signedXml);

            // 2.发送请求到微信支付系统
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();
            // 3.处理返回结果并返回
            String content = httpClient.getContent();
            System.out.println("微信支付系统 统一下单 返回的内容为：" + content);

            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
             // 业务操作结果
            returnMap.put("result_code", resultMap.get("result_code"));
            // 支付二维码地址
            returnMap.put("code_url", resultMap.get("code_url"));
            returnMap.put("totalFee", totalFee);
            returnMap.put("outTradeNo", outTradeNo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnMap;
    }

    @Override
    public Map<String, String> queryPayStatus(String outTradeNo) {
        try {
          // 1.组合要发送的参数
            Map<String , String> param=new HashMap<>();

            param.put("appid",appid);

            param.put("mch_id",partner);

            param.put("nonce_str",WXPayUtil.generateNonceStr());
            //param.put("sign", "");
            param.put("out_trade_no",outTradeNo);

            //转换成为签名了的xml内容
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);

            System.out.println("发送到微信支付系统 查询状态 的请求内容为：" + signedXml);

            //2、发送请求到微信支付系统
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();

            //3、处理返回结果并返回
            String content = httpClient.getContent();
            System.out.println("微信支付系统 查询状态 返回的内容为：" + content);

            return WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
