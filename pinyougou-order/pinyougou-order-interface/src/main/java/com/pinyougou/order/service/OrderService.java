package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface OrderService extends BaseService<TbOrder> {

    PageResult search(Integer page, Integer rows, TbOrder order);

    /**
     * 保存订单、明细、支付日志到数据库中
     * @param order
     * @return
     */
    String saveOrder(TbOrder order);

    /**
     * 根据支付日志id查询支付日志
     * @param outTradeNo
     * @return
     */
    TbPayLog findPayLogById(String outTradeNo);


    /**
     *
     * @param outTradeNo
     * @param transaction_id
     */
    void updateOrderStatus(String outTradeNo, String transaction_id);
}