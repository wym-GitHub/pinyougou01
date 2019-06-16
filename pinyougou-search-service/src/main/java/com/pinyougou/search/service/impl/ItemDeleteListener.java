package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.Arrays;

@Component

public class ItemDeleteListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;


    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long[] object = (Long[]) objectMessage.getObject();
            System.out.println("接受到删除的参数");
            itemSearchService.deleteByGoodsIds(Arrays.asList(object));
            System.out.println("删除成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
