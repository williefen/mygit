package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/23
 * @Param $param
 **/

public interface SeckillGoodsService extends BaseService<TbSeckillGoods> {

    PageResult search(Integer page, Integer rows, TbSeckillGoods seckillGoods);

    /**
     * 查询审核通过（status=1）、库存量大于0、开始时间小于等于当前时间、结束时间大于当前时间的那些秒杀商品并且按照开始时间升序排序
     * @return
     */
    List <TbSeckillGoods> findList();

    /**
     * 根据秒杀商品id到redis中查询秒杀商品
     * @param id
     * @return
     */
     TbSeckillGoods findSeckillGoodsInRedisById(Long id);

}
