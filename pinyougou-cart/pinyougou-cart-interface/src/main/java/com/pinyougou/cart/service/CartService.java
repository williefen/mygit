package com.pinyougou.cart.service;

import com.pinyougou.vo.Cart;

import java.util.List;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/19
 * @Param $param
 **/
public interface CartService {
    /**
     * 增减购物车购买商品数量
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    List<Cart> addCartToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 根据用户名查询在redis中的购物车数据
     * @param username
     * @return
     */
    List<Cart> findCartListInRedisByUsername(String username);


    /**
     * 将购物车列表保存到redis中
     * @param cartList
     * @param username
     */
    void saveCartListToRedis(List<Cart> cartList, String username);

    /**
     * 合并两个列表数据
     * @param cartList1
     * @param cartList2
     * @return
     */

    List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);
}
