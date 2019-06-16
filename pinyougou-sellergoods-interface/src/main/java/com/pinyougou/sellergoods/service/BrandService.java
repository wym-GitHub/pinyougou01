package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {
        //查询所有品牌
    public List<TbBrand>findAll();

    //分页查询
    public PageResult findPage(int pageNum,int pageSize);


        //品牌新增
    public void insert(TbBrand tbBrand);


    //1.修改操作
    //1.1先根据id查询出品牌信息放到编辑框里面
    public TbBrand findById(Long id);

    //1.2 把点击保存按钮,把编辑框里面的数据提交给后台进行更新

    public void update(TbBrand tbBrand);


    //删除操作

    public void delete(Long[] ids);

    //条件查询,重载分页查询方法,参数除了当前页,和显示的条数,还有前端给出的查询条件

    public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);

    //查询所有品牌封装成map,用于模板表的下拉框使用,name属性要改名为text
    public List<Map> selectBrandList();

}
