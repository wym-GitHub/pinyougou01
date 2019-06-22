package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
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
    public Map createNative() {
        //获取当前用户
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //查询订单
        TbSeckillOrder tbSeckillOrder = seckillOrderService.searchOrderFromRedisByUserId(name);

        //判断秒杀订单是否存在
        if (tbSeckillOrder != null) {
            long fee = (long) (tbSeckillOrder.getMoney().doubleValue() * 100);

            Map aNative = weixinPayService.createNative(tbSeckillOrder.getId() + "", fee + "");
            return aNative;
        } else {
            return new HashMap();
        }


    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Result result = null;
        int x = 0;
        while (true) {

            Map map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {
                result = new Result(false, "支付出错");
                break;
            }

            if (map.get("trade_state").equals("SUCCESS")) {//如果成功
                result = new Result(true, "支付成功");
                seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id") + "");
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            x++;
            if (x > 100) {
                result = new Result(false, "二维码超时");

                Map payresult = weixinPayService.closePay(out_trade_no);
                if (!"SUCCESS".equals(payresult.get("result_code"))) {//如果返回结果是正常关闭

                    if ("ORDERPAID".equals(payresult.get("err_code"))) {
                        result = new Result(true, "支付成功");
                        seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id") + "");
                    }
                }


                if (result.isSuccess() == false) {
                    System.out.println("超时，取消订单");
//2.调用删除
                    seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
                }
                break;
            }
        }

        return result;
    }

}
