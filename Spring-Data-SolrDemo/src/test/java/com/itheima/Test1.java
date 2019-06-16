package com.itheima;

import cn.itcast.domain.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-solr.xml")
public class Test1 {
    @Autowired
    private SolrTemplate solrTemplate;
    @Test
    public void test1(){
        TbItem tbItem = new TbItem();
        tbItem.setId(1L);
        tbItem.setBrand("华为");
        tbItem.setCategory("智能手机");
        tbItem.setGoodsId(1L);
        tbItem.setSeller("华为旗舰店");
        tbItem.setTitle("华为mate9");
        tbItem.setPrice(new BigDecimal(3000.1));
        //保存到solr库,单个保存
        solrTemplate.saveBean(tbItem);
        //必须提交才能完成保存
        solrTemplate.commit();

    }
    @Test
    //向索引库中插入多条数据
    public void test2(){
        List<TbItem> itemList=new ArrayList<>();
        for(int i=0;i<100;i++){
            TbItem tbItem = new TbItem();
            tbItem.setId(i+1L);
            tbItem.setBrand("华为");
            tbItem.setCategory("智能手机");
            tbItem.setGoodsId(i+1L);
            tbItem.setSeller("华为旗舰店");
            tbItem.setTitle("华为mate"+i);
            tbItem.setPrice(new BigDecimal(3000.1+i));
            itemList.add(tbItem);
        }
        solrTemplate.saveBeans(itemList);
        //保存到solr库,保存集合这里需要使用savebeans

        //必须提交才能完成保存
        solrTemplate.commit();

    }
    @Test
    public void querytest1(){
        TbItem byId = solrTemplate.getById(1, TbItem.class);
        System.out.println(byId.getTitle());
    }
    @Test
    public void deletest1(){
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }
    @Test
    //分页查询
    public void querytest2(){

        Query query = new SimpleQuery("*:*");
        query.setOffset(0);//设置开始索引
        query.setRows(20);//设置查询条数
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数"+tbItems.getTotalElements());
        System.out.println("总页数"+tbItems.getTotalPages());

        List<TbItem> content = tbItems.getContent();//获得查询的结果
        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle());
        }
    }

    @Test
    // //条件查询
    public void querytest3(){

        Query query = new SimpleQuery("*:*");
        //设置查询条件
        Criteria criteria=new Criteria("item_title").contains("1");
////        criteria.and("item_title");//contains不会分词
//        //criteria.and("item_category").is("小米手机");//is可以对查询字段进行分词
//
        query.addCriteria(criteria);//查询的时候把条件加上
//
//        query.setOffset(0);//设置开始索引
//        query.setRows(20);//设置查询条数
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数"+tbItems.getTotalElements());
        System.out.println("总页数"+tbItems.getTotalPages());

        List<TbItem> content = tbItems.getContent();//获得查询的结果
        for (TbItem tbItem : content) {
            System.out.println(tbItem.getTitle());
        }
    }
    @Test
    public void deleAlltest1(){
        //删除所有
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);

        solrTemplate.commit();
    }

}
