package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import javax.management.Query;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    private void importItem2Solr(){
        TbItemExample itemExample=new TbItemExample();
        TbItemExample.Criteria criteria = itemExample.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = itemMapper.selectByExample(itemExample);
        System.out.println("--打印查询结果--");
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem.getTitle());

            Map map = JSON.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(map);


        }
        System.out.println("--结束--");

        solrTemplate.saveBeans(tbItems);

//        org.springframework.data.solr.core.query.Query query=new SimpleQuery("*:*");
//        solrTemplate.delete(query);
        solrTemplate.commit();

    }

    public static void main(String[] args) {

        ApplicationContext app=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) app.getBean("solrUtil");

        solrUtil.importItem2Solr();

    }

}
