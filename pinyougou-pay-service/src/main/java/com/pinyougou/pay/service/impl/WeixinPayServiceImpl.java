package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.utils.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;

    private String notifyurl;


    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1.创建参数
        Map map = new HashMap();
        map.put("appid", appid);//公众账号ID
        map.put("mch_id", partner);//商户号
        map.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        map.put("body", "品优购");//商品描述
        map.put("out_trade_no", out_trade_no);//商品订单号
        map.put("total_fee", total_fee);//订单总金额,单位为分
        map.put("spbill_create_ip", "127.0.0.1");//用户的客户端ip
        map.put("notify_url", "http://test.itcast.cn");//异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数
        map.put("trade_type", "NATIVE");//交易类型
        try {
            //生成要发送的xml
            String xmlParam = WXPayUtil.generateSignedXml(map, partnerkey);
            System.out.println(xmlParam);

            //使用httpclient发送数据
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            //3.获得结果
            String content = httpClient.getContent();
            System.out.println(content);
            Map<String, String> stringStringMap = WXPayUtil.xmlToMap(content);
            //创建一个map,用于封装返回的数据,给前台
            Map returnMap=new HashMap();
            returnMap.put("code_url", stringStringMap.get("code_url"));//用于生成支付二维码，然后提供给用户进行扫码支付。
            returnMap.put("total_fee", total_fee);//总金额
            returnMap.put("out_trade_no", out_trade_no);//订单号
            return returnMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }




    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        //1.封装数据
        Map param=new HashMap();
        param.put("appid", appid);
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//商户订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/orderquery";

        //生出传输的xml数据
        try {
            String s = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(s);
            httpClient.post();

            //获得返回结果
            String content = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }



    }
}
