package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;
    @Reference
    private SeckillOrderService seckillOrderService;

    //生成二维码
    @RequestMapping("/createNative")
    public Map createNative(){
        //获取当前用户
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //查询订单
        TbSeckillOrder tbSeckillOrder = seckillOrderService.searchOrderFromRedisByUserId(name);

        //判断秒杀订单是否存在
        if(tbSeckillOrder!=null){
            long fee = (long) (tbSeckillOrder.getMoney().doubleValue() * 100);

            Map aNative = weixinPayService.createNative(tbSeckillOrder.getId() + "", fee + "");
            return aNative;
        }else{
            return new HashMap();
        }


    }

}
