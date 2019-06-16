package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.data.solr.core.SolrTemplate;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbSellerMapper sellerMapper;

    @Override
    public void add(Goods goods) {
        TbGoods tbgoods = goods.getGoods();
        tbgoods.setAuditStatus("0");
        //插入goods
        goodsMapper.insert(tbgoods);

        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(tbgoods.getId());//把goods里面的id赋给goodsDesc的GoodsId
        goodsDescMapper.insert(goodsDesc);

        saveItemList(goods);

    }

    private void saveItem(Goods goods, TbItem tbItem) {

        tbItem.setGoodsId(goods.getGoods().getId());//设置商品id
        tbItem.setCategoryid(goods.getGoods().getCategory3Id());//设置分类id
        tbItem.setSellerId(goods.getGoods().getSellerId());
        tbItem.setIsDefault("0");
        tbItem.setUpdateTime(new Date());//设置日期
        tbItem.setCreateTime(new Date());
        //品牌名称
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        tbItem.setBrand(tbBrand.getName());
        //分类名称
        TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        //设置商家名称
        tbItem.setCategory(tbItemCat.getName());
        TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        tbItem.setSeller(tbSeller.getNickName());
        //图片地址
        List<Map> maps = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (maps.size() > 0) {
            tbItem.setImage((String) maps.get(0).get("url"));
        }

    }

    //插入sku表,共有方法
    public void saveItemList(Goods goods) {
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            for (TbItem tbItem : goods.getItemList()) {
                String title = goods.getGoods().getGoodsName();
                Map<String, Object> map = JSON.parseObject(tbItem.getSpec(), Map.class);
                for (String key : map.keySet()) {
                    title += "" + map.get(key);
                }
                tbItem.setTitle(title);//设置标题
                saveItem(goods, tbItem);
                itemMapper.insert(tbItem);

            }
        } else {
            TbItem tbItem = new TbItem();
            tbItem.setTitle(goods.getGoods().getGoodsName());
            tbItem.setStatus("0");
            tbItem.setIsDefault("1");
            tbItem.setNum(999);
            tbItem.setPrice(goods.getGoods().getPrice());
            tbItem.setSpec("{}");
            saveItem(goods, tbItem);
            itemMapper.insert(tbItem);
        }

    }

    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        TbGoods tbGoods = goods.getGoods();
//		tbGoods.setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(tbGoods);
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDescMapper.updateByPrimaryKey(goodsDesc);

        //更新sku表,先删除原先的sku,重新插入
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andGoodsIdEqualTo(tbGoods.getId());
        itemMapper.deleteByExample(tbItemExample);

        saveItemList(goods);//插入sku

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);

        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);

        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();

        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);

        Goods goods = new Goods();
        goods.setItemList(tbItems);
        goods.setGoodsDesc(tbGoodsDesc);
        goods.setGoods(tbGoods);

        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods tbGoods = new TbGoods();
            tbGoods.setId(id);
            tbGoods.setIsDelete("1");
            goodsMapper.updateByPrimaryKeySelective(tbGoods);


        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }

            criteria.andIsDeleteIsNull();


        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override

    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(goods);

           TbItemExample tbItemExample=new TbItemExample();
            TbItemExample.Criteria criteria = tbItemExample.createCriteria();
            criteria.andGoodsIdIn(Arrays.asList(ids));
            List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
            for (TbItem tbItem : tbItems) {
                tbItem.setStatus(status);
                itemMapper.updateByPrimaryKey(tbItem);
            }


        }
    }

    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {

        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));
        criteria.andStatusEqualTo(status);
        List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
        for (TbItem tbItem : tbItems) {
            System.out.println(tbItem.getTitle());
        }

        return tbItems;
    }
}
