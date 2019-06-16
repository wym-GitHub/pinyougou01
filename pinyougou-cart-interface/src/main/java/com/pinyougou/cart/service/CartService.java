package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

public interface CartService {

    //添加商品到购物车

    public List<Cart> addGoodsToCartList(List<Cart> list,Long itemId,Integer num);

    //从redis里面查询购物车

    public List<Cart> findCartListFromRedis(String name);


    //将购物车保存到redis里面
    public void saveCartListToRedis(String username,List<Cart> cartList);

    //合并购物车

    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
