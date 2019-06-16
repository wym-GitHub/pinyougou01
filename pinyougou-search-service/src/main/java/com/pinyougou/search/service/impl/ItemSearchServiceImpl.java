package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;


    @Override
    public Map<String, Object> search(Map searchMap) {
        if(searchMap.get("keywords").equals("")){
            return null;
        }
        //处理关键字的空格
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));

        Map<String, Object> map = new HashMap<>();
        map.putAll(searchList(searchMap));//把从私有方法查询到的高亮map集合,追加到map中
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", searchCategoryList(searchMap));//分组查询获取,分类集合
        String category = (String) searchMap.get("category");//获取前台提交的搜索对象中的商品分类
        if("".equals(category)){
            //没有商品分类名称就按照分组查询得到的分类集合中的第一个分类名称查询模板;
            if(categoryList.size()>0){
                Map brandAndSpecListMap = searchBrandAndSpecList(categoryList.get(0));
                map.putAll(brandAndSpecListMap);
            }
        }else{
            Map brandAndSpecListMap = searchBrandAndSpecList(category);
            map.putAll(brandAndSpecListMap);
        }


        return map;

    }

    //查询
    public Map searchList(Map searchMap) {

        Map<String, Object> map = new HashMap<>();

        HighlightQuery highlightQuery = new SimpleHighlightQuery();
        //设置高亮选项
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮的域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//设置前缀
        highlightOptions.setSimplePostfix("</em>");//设置后缀
        highlightQuery.setHighlightOptions(highlightOptions);
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        highlightQuery.addCriteria(criteria);
        //按分类筛选
        if(!"".equals(searchMap.get("category"))){
            Criteria fileterCriteria=new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(fileterCriteria);

            highlightQuery.addFilterQuery(simpleFilterQuery);

        }
        //按品牌晒选
        if(!"".equals(searchMap.get("brand"))){
            Criteria fileterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(fileterCriteria);
            highlightQuery.addFilterQuery(simpleFilterQuery);
        }
        //过滤规格
        if(searchMap.get("spec")!=null){
            Map<String,String> spec = (Map) searchMap.get("spec");
            for (String s : spec.keySet()) {
                Criteria fileterCriteria=new Criteria("item_spec_"+s).is(spec.get(s));
                SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(fileterCriteria);
                highlightQuery.addFilterQuery(simpleFilterQuery);

            }

        }
        //价格过滤
        if(!"".equals(searchMap.get("price"))){
            String price = (String) searchMap.get("price");
            String[] split = price.split("-");
            if(!"0".equals(split[0])){
                Criteria fileterCriteria=new Criteria("item_price").greaterThanEqual(split[0]);

                FilterQuery filterQuery = new SimpleFilterQuery(fileterCriteria);
                highlightQuery.addFilterQuery(filterQuery);
            }
            if(!"*".equals(split[1])){
                Criteria fileterCriteria=new Criteria("item_price").lessThanEqual(split[1]);

                FilterQuery filterQuery = new SimpleFilterQuery(fileterCriteria);
                highlightQuery.addFilterQuery(filterQuery);
            }
        }
        //排序
        String sort = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");
        if(!"".equals(sort)&&!"".equals(sortField)){
            if(sort.equals("ASC")){
                Sort orders = new Sort(Sort.Direction.ASC,"item_"+sortField);
                highlightQuery.addSort(orders);
            }
            if(sort.equals("DESC")){
                Sort orders = new Sort(Sort.Direction.DESC,"item_"+sortField);
                highlightQuery.addSort(orders);
            }
        }

        //分页查询
        Integer pageNo = (Integer) searchMap.get("pageNo");//提取页码
        if(pageNo==null){
            pageNo=1;//设置默认值为1
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");//每页记录数
        if(pageSize==null){
            pageSize=20;
        }
        highlightQuery.setOffset((pageNo-1)*pageSize);//从第几条记录查询
        highlightQuery.setRows(pageSize);

        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);

        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();//获得高亮集合入口,根据关键字查到的所有记录
        //每一个entry里面包含没有高亮的实体和,设置高亮的实体,但是高亮有多域,和多值的问题,每一个实体设置高亮后会对应多条高亮,
        for (HighlightEntry<TbItem> h : highlighted) {
            TbItem entity = h.getEntity();//获得没有高亮的实体
            List<HighlightEntry.Highlight> highlights = h.getHighlights();

            for (HighlightEntry.Highlight highlight : highlights) {
                List<String> snipplets = highlight.getSnipplets();//每个域可能存在多值

//                System.out.println(snipplets);
            }
            if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) {
                //判断是否有高亮域的个数大于一,并且高亮域有值
                entity.setTitle(highlights.get(0).getSnipplets().get(0));

            }


        }
        map.put("rows", tbItems.getContent());
        map.put("totalPages", tbItems.getTotalPages());//返回总页数
        map.put("total", tbItems.getTotalElements());//返回总记录数
        return map;
    }


        //根据前台提交的搜索关键字,获得商品分类
    public List<String> searchCategoryList(Map searchMap) {

        List<String> list = new ArrayList<>();
        Query query = new SimpleQuery();
        //设置查询关键字
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));

        query.addCriteria(criteria);//添加查询条件
        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);

//        List<TbItem> content = tbItems.getContent();getContent在这里无法使用
        //按照指定的分组的域,得到分组结果集,
        GroupResult<TbItem> item_category = tbItems.getGroupResult("item_category");

        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = item_category.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();


        for (GroupEntry<TbItem> entry : content) {
            String groupValue = entry.getGroupValue();
            list.add(groupValue);//分组结果的名称封装到返回值集合中

        }
        return list;
    }

    @Autowired
    private RedisTemplate redisTemplate;
        //根据商品分类名称,获取缓存中模板对应的品牌集合和,规格集合
    public Map searchBrandAndSpecList(String category) {
            Map map=new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//根据分类名称获取模板id

        if(typeId!=null){

            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);//从缓存中,根据模板id获取品牌列表
            map.put("brandList", brandList);

            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);//从缓存中获取,规格列表
            map.put("specList", specList);

        }
        return map;
    }

    @Override
    public void importList(List list) {
       solrTemplate.saveBeans(list );
       solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List list) {
        System.out.println("删除商品id"+list);
        Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(list);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();

    }
}
