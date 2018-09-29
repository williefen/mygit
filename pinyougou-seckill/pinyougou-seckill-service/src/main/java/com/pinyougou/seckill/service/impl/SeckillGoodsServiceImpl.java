package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service(interfaceClass = SeckillGoodsService.class)
public class SeckillGoodsServiceImpl extends BaseServiceImpl<TbSeckillGoods> implements SeckillGoodsService {
    //秒杀商品在redis中的key的名称
    public static final String SECKILL_GOODS = "SECKILL_GOODS";
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult search(Integer page, Integer rows, TbSeckillGoods seckillGoods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(seckillGoods.get***())){
            criteria.andLike("***", "%" + seckillGoods.get***() + "%");
        }*/

        List<TbSeckillGoods> list = seckillGoodsMapper.selectByExample(example);
        PageInfo<TbSeckillGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<TbSeckillGoods> findList() {

            List<TbSeckillGoods> seckillGoodsList = null;

        try {
            seckillGoodsList=redisTemplate.boundHashOps(SECKILL_GOODS).values();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (seckillGoodsList == null || seckillGoodsList.size() == 0) {
            // 执行本方法
            Example example = new Example(TbSeckillGoods.class);

            Example.Criteria criteria = example.createCriteria();

            criteria.andEqualTo("status", "1");

            criteria.andGreaterThan("stockCount", "0");

            criteria.andLessThanOrEqualTo("startTime", new Date());

            criteria.andGreaterThan("endTime", new Date());
            // 根据开始时间升序排序
            example.orderBy("startTime");

            seckillGoodsList = seckillGoodsMapper.selectByExample(example);

                try {
                    //存入redis
                    for (TbSeckillGoods seckillGoods:seckillGoodsList){
                        redisTemplate.boundHashOps(SECKILL_GOODS).put(seckillGoods.getId(),seckillGoods);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("从缓存中读取了秒杀商品列表...");
            }
                return seckillGoodsList;
        }

    @Override
    public TbSeckillGoods findSeckillGoodsInRedisById(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS).get(id);
    }
}
