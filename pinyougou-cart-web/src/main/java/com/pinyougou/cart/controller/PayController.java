package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.utils.IdWorker;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    //注入二维码生成服务
    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;
    @RequestMapping("/createNative")

    public Map createNative() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog tbPayLog = orderService.searchPayLogFromRedis(name);
        //判断日志是否存在
        if(tbPayLog==null){
            return new HashMap();
        }else{
            return weixinPayService.createNative(tbPayLog.getOutTradeNo(), tbPayLog.getTotalFee()+"");

        }


    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result = null;
        int x = 0;
        while (true) {
            Map map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {
                result = new Result(false, "支付出错");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")) {
                result = new Result(true, "支付成功");

                //修改订单状态
                orderService.updateOrderStatus(out_trade_no, (String) map.get("transaction_id"));

                break;
            }
            //设置间隔三秒查询

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            if (x >=100) {
                result = new Result(false, "二维码超时");
                break;
            }
        }
        System.out.println(result.getMassage());
        return result;

    }
}
