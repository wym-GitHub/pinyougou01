package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper tbBrandMapper;


    @Override  //查询所有
    public List<TbBrand> findAll() {
        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(null);
        return tbBrands;
    }

    @Override  //分页查询
    public PageResult findPage(int pageNum, int pageSize) {
        //执行sql语句之前,分页插件会拦截,并在sql语句后面加上limit关键字(mysql数据库关键字)
        PageHelper.startPage(pageNum, pageSize);
        //执行sql查询

        Page tbBrands = (Page) tbBrandMapper.selectByExample(null);
        List result = tbBrands.getResult();
        long total = tbBrands.getTotal();

        PageResult pageResult=new PageResult();
        pageResult.setRows(result);
        pageResult.setTotal(total);

        return pageResult;
    }

    @Override //品牌新增
    public void insert(TbBrand tbBrand) {

        tbBrandMapper.insert(tbBrand);

    }

    @Override //修改操作第一步:根据id查询出当前品牌,把品牌信息放到编辑框里
    public TbBrand findById(Long id) {

        TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(id);
        return tbBrand;
    }

    @Override//修改操作第二步:点击保存,把修改后的信息,提交给后台
    public void update(TbBrand tbBrand) {
            tbBrandMapper.updateByPrimaryKey(tbBrand);
    }


    //删除操作
    @Override
    public void delete(Long[] ids) {
        //遍历前台传递的id数组,进行删除
        for (Long id : ids) {

             tbBrandMapper.deleteByPrimaryKey(id);
        }


    }

    //条件查询,重载分页查询方法,参数除了当前页,和显示的条数,还有前端给出的查询条件
    @Override
    public PageResult findPage(TbBrand tbBrand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        TbBrandExample tbBrandExample = new TbBrandExample();
        TbBrandExample.Criteria criteria = tbBrandExample.createCriteria();
        //判断前端传来的tbBrand里面的条件
        if(tbBrand!=null){
            if(tbBrand.getName()!=null&&tbBrand.getName().length()>0){
                criteria.andNameLike("%"+tbBrand.getName()+"%");
            }
            if(tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
            }
        }

        Page<TbBrand> tbBrands = (Page<TbBrand>) tbBrandMapper.selectByExample(tbBrandExample);


        return new PageResult(tbBrands.getTotal(),tbBrands.getResult());
    }

    @Override
    public List<Map> selectBrandList() {
        List<Map> maps = tbBrandMapper.selectBrandList();
        return maps;
    }
}
