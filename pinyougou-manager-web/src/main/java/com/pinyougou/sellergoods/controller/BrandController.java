package com.pinyougou.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    //查询所有品牌
    @RequestMapping("findall")
    public List<TbBrand> findAll() {

        List<TbBrand> all = brandService.findAll();
        System.out.println("sucd");
        return all;


    }

    //分页查询
    @RequestMapping("page")
    public PageResult findPage(int pageNum, int pageSize) {
        PageResult pageResult = brandService.findPage(pageNum, pageSize);

        return pageResult;
    }

    //品牌新增
    @RequestMapping("insert")
    public Result insertBrand(@RequestBody TbBrand tbBrand) {


        try {
            brandService.insert(tbBrand);
            return new Result(true, "增加成功");
        } catch (Exception e) {

            e.printStackTrace();
            return new Result(false, "增加失败");
        }


    }

    //修改操作:1.查询
    @RequestMapping("findById")
    public TbBrand findById(Long id){
        TbBrand byId = brandService.findById(id);
            return byId;
    }

    //修改操作:2:接受修改后的数据,进行更新操作
    @RequestMapping("update")
    public Result update(@RequestBody TbBrand tbBrand){
        try {
            brandService.update(tbBrand);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }

    }

    //删除操作
    @RequestMapping("delete")
    public Result delete(Long[] ids){

        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    //条件查询
    @RequestMapping("search")
    public PageResult selectByCondition(@RequestBody TbBrand tbBrand,int pageNum,int pageSize){
        PageResult page = brandService.findPage(tbBrand, pageNum, pageSize);

        return page;

    }
    @RequestMapping("selectBrandList")
    public List<Map> selectBrandList(){
        return brandService.selectBrandList();
    }
}
