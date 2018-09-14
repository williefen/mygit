package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/13
 * @Param $param
 **/

@Controller
public class ItemController {

    @Reference
    private GoodsService goodsService;
    @Reference
    private ItemCatService itemCatService;

    /**
     * 根据商品spu id查询商品的基本、描述、sku列表等信息
     * 并跳转到商品的详情页面
     *
     * @param goodsId 商品spu id
     * @return
     */

    @GetMapping("/{goodsId}")
    public ModelAndView toItemPage(@PathVariable Long goodsId) {

        ModelAndView mv = new ModelAndView("item");

        try {
            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");
              //  商品基本信息
              mv.addObject("goods",goods.getGoods());
              //  商品描述信息
              mv.addObject("goodsDesc",goods.getGoodsDesc());
              //  查询sku商品列表
              mv.addObject("itemList",goods.getItemList());

              //一级商品分类中文名称
            TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
            mv.addObject("itemCat1",itemCat1.getName());
            //二级商品分类中文名称
            TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
            mv.addObject("itemCat2",itemCat2.getName());
              //三级商品分类中文名称
            TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
            mv.addObject("itemCat3",itemCat3.getName());



        } catch (Exception e) {
            e.printStackTrace();
        }

        return mv;
    }
}
