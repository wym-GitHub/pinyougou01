package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;

    //添加商品到购物车
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> list, Long itemId, Integer num) {
        //1.根据商品sku Id查询出sku商品信息
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        if (tbItem == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!tbItem.getStatus().equals("1")) {
            throw new RuntimeException("商品状态不合法");
        }

        //2.获取商家id
        String sellerId = tbItem.getSellerId();
        //3.根据商家id查询购物车是否存在该商家的购物车
        Cart cart = searchCartBySellerId(list, sellerId);
        if (cart == null) {
            //4.如果购物车中不存在该商家购物车
            //4.1新建一个商家购物车对象
            cart = new Cart();
            cart.setSellerName(tbItem.getSeller());
            cart.setSellerId(sellerId);
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(tbItem, num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
//4.2将新建的商家购物车对象添加到购物车列表
            list.add(cart);

        } else {  //5.如果购物车中存在该商家购物车

            //查询是否存在该商品
            TbOrderItem tbOrderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if (tbOrderItem == null) {
                //5.2如果不存在,则新增购物车明细

                TbOrderItem orderItem = createOrderItem(tbItem, num);
                cart.getOrderItemList().add(orderItem);


            } else {
                //5.1如果存在,在原购物车明细上添加数量,更新金额
                tbOrderItem.setNum(tbOrderItem.getNum()+num);

                tbOrderItem.setTotalFee(tbOrderItem.getPrice().multiply(BigDecimal.valueOf(tbOrderItem.getNum())));

                //如果数量操作小于等于0,移除
                if (tbOrderItem.getNum()<=0){
                    cart.getOrderItemList().remove(tbOrderItem);
                }
                //如果移除明细后,cast的明细数量为0,则移除该商家的购物车
                if(cart.getOrderItemList().size()<=0){
                    list.remove(cart);
                }

            }


        }


        return list;
    }

    //从redis里面查询购物车
    @Autowired
    private RedisTemplate redisTemplate;



    //根据商家id查询购物车列表中是否存在该商家购物车
    public Cart searchCartBySellerId(List<Cart> list, String sellerId) {

        for (Cart cart : list) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    //新建商家购物车
    public TbOrderItem createOrderItem(TbItem item, Integer num) {
        if (num <= 0) {
            throw new RuntimeException("数量不合法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        BigDecimal totalFee = item.getPrice().multiply(BigDecimal.valueOf(num));
        orderItem.setTotalFee(totalFee);
        return orderItem;
    }

    //根据商品id,查询商家购物车是否存在该商品

    public TbOrderItem searchOrderItemByItemId(List<TbOrderItem> list, Long itemId) {
        for (TbOrderItem tbOrderItem : list) {

            if (tbOrderItem.getItemId().equals(itemId)) {
                return tbOrderItem;
            }
        }
        return null;
    }



    @Override
    public List<Cart> findCartListFromRedis(String name) {
        System.out.println("从redis里面查询购物车"+name);

        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(name);

        if(cartList==null){
            cartList=new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis里面存储数据"+username);
        redisTemplate.boundHashOps("cartList").put(username, cartList);



    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {

        for (Cart cart : cartList2) {
            System.out.println("合并购物车");
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem tbOrderItem : orderItemList) {
                cartList1=addGoodsToCartList(cartList1, tbOrderItem.getItemId(), tbOrderItem.getNum());

            }

        }


        return cartList1;
    }
}
