package com.pinyougou.item.activemq.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/15
 * @Param $param
 **/
public class ItemDeleteTopicMessageListener extends AbstractAdaptableMessageListener {
     //
     @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;
    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //1、接收消息
        ObjectMessage objectMessage = (ObjectMessage) message;

        Long[] ids = (Long[]) objectMessage.getObject();

        //2、处理消息（根据商品spu id删除指定路径下具体的静态页面）
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                File file = new File(ITEM_HTML_PATH + id + ".html");
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }
}
