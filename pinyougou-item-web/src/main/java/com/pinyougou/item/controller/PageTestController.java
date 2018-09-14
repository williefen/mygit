package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/14
 * @Param $param
 **/
@RequestMapping("/test")
@RestController
public class PageTestController {
  @Autowired
  private FreeMarkerConfigurer freeMarkerConfigurer;

  @Reference
  private GoodsService goodsService;
  @Reference
  private ItemCatService itemCatService;
  // 读取配置文件中的配置项
    @Value("${ITEM_HTML_PATH}")
  private String ITEM_HTML_PATH;

    @GetMapping("/audit")
    public String auditGoods(Long[] goodIds){

        if(goodIds!=null&&goodIds.length>0){
            for (Long goodsId:goodIds){
                 genHtml(goodsId);
            }
        }
        return  "success";
    }

    /**
     * 模拟批量删除商品；并且删除商品对应生成在指定路径下的静态页面
     * @param goodsIds
     */

@GetMapping("/delete")
  public  String deleteGoods(Long[] goodsIds) {

        if (goodsIds != null && goodsIds.length > 0) {
            for (Long goodsId : goodsIds) {
                File file = new File(ITEM_HTML_PATH + goodsId+ ".html");
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        return "success";
    }

    // 生成商品静态页面
    private void genHtml(Long goodsId) {

        try {
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            Map<String, Object> dataMode1 = new HashMap<>();
            // 数据
            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");
            //  商品基本信息
            dataMode1.put("goods",goods.getGoods());
            //  商品描述信息
            dataMode1.put("goodsDesc",goods.getGoodsDesc());
            //  查询sku商品列表
            dataMode1.put("itemList",goods.getItemList());

            //一级商品分类中文名称
            TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
            dataMode1.put("itemCat1",itemCat1.getName());
            //二级商品分类中文名称
            TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
            dataMode1.put("itemCat2",itemCat2.getName());
            //三级商品分类中文名称
            TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
            dataMode1.put("itemCat3",itemCat3.getName());

            // 输出
            FileWriter fileWriter = new FileWriter(ITEM_HTML_PATH + goodsId + ".html");

            template.process(dataMode1,fileWriter);

            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

