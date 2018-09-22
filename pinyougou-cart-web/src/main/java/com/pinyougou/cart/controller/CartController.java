package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/19
 * @Param $param
 **/
@RequestMapping("/cart")
@RestController
public class CartController {

  private  static  final  String COOKIE_CART_LIST = "PYG_CART_LIST";
  private  static  final  int COOKIE_CART_MAX_AGE =86400;

  @Autowired
  private HttpServletRequest request;
  @Autowired
  private HttpServletResponse response;
  @Reference
  private CartService cartService;


    /**
     * 增减购物车购买商品数量
     * @param itemId
     * @param num
     * @return
     */
    @GetMapping("/addCartToCartList")
    @CrossOrigin(origins= "http://item.pinyougou.com", allowCredentials = "true")
    public Result addCartToCartList(Long itemId, Integer num){

        try {

            //允许详情系统的资源请求
            // response.setHeader("Access-Control-Allow-Origin ", "http://item.pinyougou.com");

            //允许接收详情系统前端携带的cookie
            // response.setHeader("Access-Control-Allow-Credentials", "true");

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            // 查询购物车列表
            List<Cart> cartList = findCartList();
            //将最新的购买数量添加到对应的购物车列表中
            cartList = cartService.addCartToCartList(cartList, itemId, num);
            if("anonymousUser".equals(username)) {
                // 未登录
                String cartListJsonStr = JSONArray.toJSONString(cartList);
                CookieUtils.setCookie(request, response, COOKIE_CART_LIST, cartListJsonStr, COOKIE_CART_MAX_AGE, true);
            }else{
                //已经登录；操作在redis中的购物车数据并将最新的购物车列表写回redis
                cartService.saveCartListToRedis(cartList,username);
            }
            return  Result.ok("加入购物车成功");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return  Result.fail("加入购物车失败");
    }

    /**
     * 查询登录或者未登录情况下购物车列表数据
     */
    @GetMapping("/findCartList")
    public List<Cart> findCartList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
            // 未登录：从cookie中查询购物车数据
            List<Cart> cookie_cartList = new ArrayList<>();
            //1 获取cookie中购物车数据
            String cartListJsonStr = CookieUtils.getCookieValue(request, COOKIE_CART_LIST, true);
             //2 cookie中购物车json格式字符串转换为集合
            if (!StringUtils.isEmpty(cartListJsonStr)){
                cookie_cartList = JSONArray.parseArray(cartListJsonStr, Cart.class);
            }
            if ("anonymousUser".equals(username)){
                return cookie_cartList;
            }else{
                //已经登录；从redis中查询购物车数据
                List<Cart> redis_cartList = cartService.findCartListInRedisByUsername(username);

                //合并购物车数据
                if (cookie_cartList != null && cookie_cartList.size() > 0) {
                    redis_cartList = cartService.mergeCartList(cookie_cartList, redis_cartList);

                    //将最新的购物车数据写回redis
                    cartService.saveCartListToRedis(redis_cartList, username);

                    //删除cookie中的购物车数据
                    CookieUtils.deleteCookie(request, response, COOKIE_CART_LIST);
                }
                return redis_cartList;
            }
    }

    /**
     * 获取用户名
     * @return
     */
    @GetMapping("/getUsername")
  public Map<String,Object> getUsername(){

       Map<String,Object> map=new HashMap<String,Object>();
       String username= SecurityContextHolder.getContext().getAuthentication().getName();
     // 如果未登录；那么获取到的 username 为： anonymousUser
      map.put("username",username);
      return  map;
    }
}
