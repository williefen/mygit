package com.pinyougou.item.activemq.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/15
 * @Param $param
 **/
public class ItemTopicMessageListener extends AbstractAdaptableMessageListener {
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Reference
    private GoodsService goodsService;
    @Reference
    private ItemCatService itemCatService;
    // 读取配置文件中的配置项
    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;
    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        // 接收消息
        ObjectMessage objectMessage = (ObjectMessage) message;

        Long[] ids = (Long[]) objectMessage.getObject();
        // 处理信息
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                genHtml(id);
            }
        }
    }
           // 生成静态页面
    private  void  genHtml(Long goodsId){

        try {
            // 模板
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
