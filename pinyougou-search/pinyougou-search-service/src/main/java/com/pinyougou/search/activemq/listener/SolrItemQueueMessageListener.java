package com.pinyougou.search.activemq.listener;

import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/15
 * @Param $param
 **/
public class SolrItemQueueMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        try {
            //1、接收消息
            TextMessage textMessage = (TextMessage) message;
            //将json格式字符串转换为java集合对象
            List<TbItem> itemList = JSONArray.parseArray(textMessage.getText(), TbItem.class);
            //2、处理消息；把商品数据同步保存到solr中
            itemSearchService.importItemList(itemList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
