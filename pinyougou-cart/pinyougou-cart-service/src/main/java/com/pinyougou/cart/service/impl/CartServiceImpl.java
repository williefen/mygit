package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/19
 * @Param $param
 **/
@Service(interfaceClass = CartService.class)
public class CartServiceImpl implements CartService {
    //购物车在redis中对应的key的名称
    private static final String REDIS_CART_LIST = "CART_LIST";
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addCartToCartList(List<Cart> cartList, Long itemId, Integer num) {
        // 1
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        // 2
        if (!"1".equals(item.getStatus())) {

            throw new RuntimeException("商品不合法");
        }

        Cart cart = findCartBySellerId(cartList, item.getSellerId());
        // 3
        if (cart == null) {
            if (num > 0) {
                cart = new Cart();
                cart.setSellerId(item.getSellerId());
                cart.setSeller(item.getSeller());
                // 创建商品列表
                List<TbOrderItem> orderItemList = new ArrayList<>();

                TbOrderItem orderItem = createOrderItem(item, num);
                orderItemList.add(orderItem);

                cart.setOrderItemList(orderItemList);
                cartList.add(cart);
            } else {
                throw new RuntimeException("购买数量非法");
            }
        } else {
            // 4如果购买商品对应的商家是存在的；
            TbOrderItem orderItem = findOrderItemByItemId(cart, itemId);
            if (orderItem != null) {
                //4.1
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                // 4.1.1
                if (orderItem.getNum() < 1) {
                    cart.getOrderItemList().remove(orderItem);
                }
                // 4.1.2
                if (cart.getOrderItemList().size() < 1) {
                    cartList.remove(cart);
                }
                } else {
                    // 4.2
                    orderItem = createOrderItem(item, num);
                    cart.getOrderItemList().add(orderItem);
                }
            }
            return cartList;
        }
    @Override
    public List<Cart> findCartListInRedisByUsername(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(username);

        if(cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(List<Cart> cartList, String username) {
        redisTemplate.boundHashOps(REDIS_CART_LIST).put(username,cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
         if (cartList1 != null&&cartList1.size() >0){
             for (Cart cart : cartList1){
                 for (TbOrderItem orderItem:cart.getOrderItemList()){
                    cartList2=addCartToCartList(cartList2,orderItem.getItemId(),orderItem.getNum());
                 }
             }
         }
        return cartList2;
    }

    /**
     * 在购物车商品列表中根据商品id查询具体的OrderItem
     * @param cart
     * @param itemId
     * @return
     */
    private TbOrderItem findOrderItemByItemId(Cart cart, Long itemId) {
        if (cart.getOrderItemList() != null && cart.getOrderItemList().size() > 0) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                if (itemId.equals(orderItem.getItemId())) {
                    return orderItem;
                }
            }
        }
        return null;
    }

    /**
     * 将商品sku转换为orderItem
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem=new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());

        double totalFee=item.getPrice().doubleValue() * num;
        orderItem.setTotalFee(new BigDecimal(totalFee));
        return  orderItem;
    }

    /**
     * 在购物车列表中根据商家id查询购物车cart
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart findCartBySellerId(List<Cart> cartList, String sellerId) {
        if (cartList != null && cartList.size() > 0) {
            for (Cart cart : cartList) {
                if (sellerId.equals(cart.getSellerId())) {
                    return cart;
                }
            }
        }
        return null;
    }
}
