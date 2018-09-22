package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl extends BaseServiceImpl<TbOrder> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
     private PayLogMapper payLogMapper;

    //购物车在redis中对应的key的名称
    private static final String REDIS_CART_LIST = "CART_LIST";
    @Override
    public PageResult search(Integer page, Integer rows, TbOrder order) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(order.get***())){
            criteria.andLike("***", "%" + order.get***() + "%");
        }*/

        List<TbOrder> list = orderMapper.selectByExample(example);
        PageInfo<TbOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public String saveOrder(TbOrder order) {
        String outTradeNo="";
          //1
        List<Cart> cartList= (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(order.getUserId());
        if (cartList !=null&& cartList.size() >0){
        //2 .
      Long orderId=0L;
      String orderIds="";
      double totalFee=0.0;
            for (Cart cart : cartList) {
            // 创建一个订单对象
                TbOrder tbOrder=new TbOrder();
                orderId=idWorker.nextId();
                tbOrder.setOrderId(orderId);
                tbOrder.setSourceType(order.getSourceType());
                tbOrder.setUserId(order.getUserId());
                tbOrder.setCreateTime(new Date());
                tbOrder.setUpdateTime(tbOrder.getCreateTime());
               // 支付方式
                tbOrder.setPaymentType(order.getPaymentType());
                // 收件人信息
                tbOrder.setReceiver(order.getPaymentType());
                tbOrder.setReceiverAreaName(order.getReceiverAreaName());
                tbOrder.setReceiverMobile(order.getReceiverMobile());

                tbOrder.setSellerId(cart.getSellerId());
                //未支付
                tbOrder.setStatus("0");
                //
                double payment=0.0;
                for (TbOrderItem orderItem : cart.getOrderItemList()) {

                    orderItem.setId(idWorker.nextId());

                    orderItem.setOrderId(orderId);

                    payment +=orderItem.getTotalFee().doubleValue();
                    // 保存明细
                    orderItemMapper.insertSelective(orderItem);
                }
                //本笔订单的实付金额
                tbOrder.setPayment(new BigDecimal(payment));
                //记录本次交易总金额
                totalFee +=payment;
                // 累计订单id
                if (orderIds.length() >0){
                    orderIds += "," +orderId;
                }else {
                    orderIds=orderId.toString();
                }
                // 保存订单
                orderMapper.insertSelective(tbOrder);
            }
              // 3.微信支付情况
                if ("1".equals(order.getPaymentType())){
                    TbPayLog payLog = new TbPayLog();

                    outTradeNo=idWorker.nextId()+"";
                    payLog.setOutTradeNo(outTradeNo);
                    payLog.setCreateTime(new Date());
                    //在线支付
                       payLog.setPayType("1");
                    // 未支付
                       payLog.setTradeState("0");
                    // 购买者
                      payLog.setUserId(order.getUserId());
                    //本次要支付的所有的订单的id，使用逗号隔开
                      payLog.setOrderList(orderIds);
                    // 本次购物要支付的总金额 = 所有订单的总金额累加
                    // 因为微信、支付宝等支付接口要求支付的金额精度为分
                      payLog.setTotalFee((long)(100*totalFee));
                      payLogMapper.insertSelective(payLog);
                }
            //4
            redisTemplate.boundHashOps(REDIS_CART_LIST).delete(order.getUserId());
        }
          //5 返回支付日志 id ；如果不是微信支付则返回空
        return outTradeNo;
    }

    @Override
    public TbPayLog findPayLogById(String outTradeNo) {
        return payLogMapper.selectByPrimaryKey(outTradeNo);
    }

    @Override
    public void updateOrderStatus(String outTradeNo, String transaction_id) {
        //1、付信息中的支付状态
        TbPayLog payLog=payLogMapper.selectByPrimaryKey(outTradeNo);
        payLog.setTransactionId(transaction_id);
         // 已支付
        payLog.setTradeState("1");
        payLog.setPayTime(new Date());

        payLogMapper.updateByPrimaryKeySelective(payLog);

        //2、本支付对应的所有订单的支付状态
        String[] orderIds=payLog.getOrderList().split(",");

         TbOrder order=new TbOrder();
         order.setStatus("2");
         order.setPaymentTime(new Date());
         order.setUpdateTime(order.getPaymentTime());

        Example example = new Example(TbOrder.class);
        example.createCriteria().andIn("orderId", Arrays.asList(orderIds));

        orderMapper.updateByExampleSelective(order,example);
    }
}
