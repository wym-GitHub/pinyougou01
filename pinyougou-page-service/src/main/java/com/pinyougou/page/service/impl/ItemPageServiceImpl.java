package com.pinyougou.page.service.impl;


import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService{

    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Override
    public boolean genItemHtml(Long goodsId) {
        //获取配置对象
        try {
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            //创建数据模型
            Map dataModel=new HashMap();
            //加载商品表数据
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods", tbGoods);
            //加载商品扩展表的数据
            TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc", tbGoodsDesc);
            //查询商品分类,放进数据模型
            String category1Name = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String category2Name = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String category3Name = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
            dataModel.put("itemCat1", category1Name);
            dataModel.put("itemCat2", category2Name);
            dataModel.put("itemCat3", category3Name);

            //查询sku数据

            TbItemExample tbItemExample=new TbItemExample();
            TbItemExample.Criteria criteria = tbItemExample.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);
            tbItemExample.setOrderByClause("is_default desc");//按照是否默认排序,保证第一个是默认的商品
            List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
            for (TbItem tbItem : tbItems) {
                System.out.println(tbItem.getTitle());
            }
            dataModel.put("itemList", tbItems);


            //创建输出流把生成的模板存到指定的位置
            Writer out=new FileWriter(pagedir+goodsId+".html");
            template.process(dataModel, out);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public boolean deleteItemHtml(Long[] ids) {

        try {
            for (Long id : ids) {

                File file=new File(pagedir+id+".html");
                file.delete();

            }
            return true;
        } catch (Exception e) {

            e.printStackTrace();

        }
        return false;

    }
}
