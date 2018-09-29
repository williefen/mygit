package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/23
 * @Param $param
 **/
public interface SeckillOrderService extends BaseService<TbSeckillOrder> {
    PageResult search(Integer page, Integer rows, TbSeckillOrder seckillOrder);

    /**
     * 根据秒杀商品id生成秒杀订单
     * @param seckillId
     * @param username
     * @return
     * @throws InterruptedException
     */
  String submitOrder(Long seckillId,String username) throws  InterruptedException;

    /**
     * 根据秒杀订单id查询redis中的秒杀订单
     * @param outTradeNo
     * @return
     */
   TbSeckillOrder findSeckillOrderInRedisByOrderId(String outTradeNo);

    /**
     * 将秒杀订单id对应的redis未支付的订单修改为已支付再将数据同步到数据库中
     * @param outTradeNo
     * @param transaction_id
     */
    void  updateSeckillOrderInRedisToDb(String outTradeNo,String transaction_id);

    /**
     *
     * @param outTradeNo
     * @throws InterruptedException
     */
    void deleteSeckillOrderInRedis(String outTradeNo) throws  InterruptedException;
}
