package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import javassist.bytecode.stackmap.BasicBlock;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/seckillOrder")
@RestController
public class SeckillOrderController {

    @Reference
    private  SeckillOrderService seckillOrderService;

    /**
     * 根据秒杀商品id生成秒杀订单
     * @param seckillId
     * @return
     */
  @GetMapping("/submitOrder")
  public  Result submitOrder(Long seckillId){

      try {
          String username= SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(username)) {
            String seckillOrderId = seckillOrderService.submitOrder(seckillId, username);
            if (!StringUtils.isEmpty(seckillOrderId)) {
                return Result.ok(seckillOrderId);
            }
            } else {

                return Result.fail("请先登录之后再秒杀");
            }
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
            return  Result.fail("秒杀失败");
  }
}
