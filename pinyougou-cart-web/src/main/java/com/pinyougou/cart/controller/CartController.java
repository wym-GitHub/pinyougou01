package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.CookieUtil;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @RequestMapping("findCartList")
    public List<Cart> findCartList() {
        //每次查询的时候,执行合并

        //获得登录名称
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        //使用cookies操作工具类,查询购物车列表
        String cartList = CookieUtil.getCookieValue(request, "cartList", "UTF-8");

        if (cartList == null || cartList.equals("")) {
            cartList = "[]";
        }
        List<Cart> carts = JSON.parseArray(cartList, Cart.class);
        if (name.equals("anonymousUser")) {
            //没有登录,读取cookie
            return carts;
        } else {
            //已经登录
            //读取redis
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);

            if(carts.size()>0){
                //cookie中有购物车记录,则执行合并操作
                List<Cart> carts1 = cartService.mergeCartList(cartListFromRedis, carts);
                //合并后,清空cookie里面的购物车
                CookieUtil.deleteCookie(request,response , "cartList");
                //将合并后的数据放入redis
                cartService.saveCartListToRedis(name, carts1);
                return carts1;

            }


            return cartListFromRedis;
        }

    }

    //添加商品到购物车
    @RequestMapping("addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num) {
        //获取登录名称
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            //一.首先查询出购物车列表
            List<Cart> cartList = findCartList();
            //执行添加操作
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if (name.equals("anonymousUser")) {
                //商家没有登录,直接保存到cookie

                //把新的购物车列表存到cookies里面

                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
                return new Result(true, "添加成功");
            } else {
                //商家登录,保存到redis里面

                cartService.saveCartListToRedis(name, cartList);
                return new Result(true, "添加成功");

            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
}
