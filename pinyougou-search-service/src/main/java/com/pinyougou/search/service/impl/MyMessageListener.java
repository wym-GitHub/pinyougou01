package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

@Component
public class MyMessageListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            System.out.println("接受到同步参数"+text);
            List<TbItem> itemList = JSON.parseArray(text, TbItem.class);
            for (TbItem tbItem : itemList) {

                Map map = JSON.parseObject(tbItem.getSpec(), Map.class);
                tbItem.setSpecMap(map);//给索引库动态域,对应字段赋值

            }

            itemSearchService.importList(itemList);


        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
