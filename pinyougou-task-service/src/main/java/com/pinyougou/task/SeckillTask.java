package com.pinyougou.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SeckillTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillGoodsMapper tbSeckillGoodsMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void refreshSeckillGoods() {
        System.out.println("执行了调度任务" + new Date());
        //查询所有的秒杀商品
        Set seckillGoods = redisTemplate.boundHashOps("seckillGoods").keys();

        //查询正在秒杀的列表
        TbSeckillGoodsExample tbSeckillGoodsExample = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = tbSeckillGoodsExample.createCriteria();
        criteria.andStockCountGreaterThan(0);
        criteria.andStatusEqualTo("1");
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        criteria.andEndTimeGreaterThan(new Date());
        if (seckillGoods.size() > 0) {
            criteria.andIdNotIn(new ArrayList<>(seckillGoods));

        }
        List<TbSeckillGoods> tbSeckillGoods = tbSeckillGoodsMapper.selectByExample(tbSeckillGoodsExample);
        if (tbSeckillGoods != null || tbSeckillGoods.size() > 0) {
            for (TbSeckillGoods tbSeckillGood : tbSeckillGoods) {
                redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGood.getId(), tbSeckillGood);
                System.out.println("增量更新" + tbSeckillGood.getId());
            }
            System.out.println("将" + tbSeckillGoods.size() + "条商品装入缓存");
        }

    }

    @Scheduled(cron = "0 * * * * ?")
    public void removeSeckillGoods() {
        System.out.println("移除秒杀商品任务在执行");
        //扫描缓存中的秒杀列表,发现过期的,从缓存中移除并同步到数据库

        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();


        for (TbSeckillGoods seckillGood : seckillGoods) {
            TbSeckillGoods tbSeckillGoods = tbSeckillGoodsMapper.selectByPrimaryKey(seckillGood.getId());
            if (tbSeckillGoods==null||(!tbSeckillGoods.getStatus().equals("1"))) {
                //判断秒杀商品是否重新修改,待审核,若待审核;则删除缓存中对应的商品
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGood.getId());
                continue;
            }
            if (seckillGood.getEndTime().getTime() < new Date().getTime()) {
                //秒杀商品已过期,把商品数据同步到数据库
                tbSeckillGoodsMapper.updateByPrimaryKey(seckillGood);
                //从缓存中移除
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGood.getId());
                System.out.println("移除秒杀商品" + seckillGood.getId());
            }

        }
        System.out.println("移除秒杀商品任务结束");
    }

}
