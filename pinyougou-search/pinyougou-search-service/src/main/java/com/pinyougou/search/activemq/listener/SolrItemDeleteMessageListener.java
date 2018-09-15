package com.pinyougou.search.activemq.listener;

import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/15
 * @Param $param
 **/
public class SolrItemDeleteMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            //1、接收消息
            Long[] ids = (Long[]) objectMessage.getObject();

            //2、处理消息；把商品数据同步保存到solr中
            itemSearchService.deleteItemByGoodsIds(Arrays.asList(ids));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
