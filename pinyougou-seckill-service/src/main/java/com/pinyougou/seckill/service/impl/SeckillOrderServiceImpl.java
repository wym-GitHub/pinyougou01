package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;


import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	@Autowired
	private TbSeckillGoodsMapper tbSeckillGoodsMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private IdWorker idWorker;
	@Override
	public void submitOrder(Long seckillId, String userId) {
		//从缓存中读取商品
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
		if(seckillGoods==null){
			throw new RuntimeException("商品不存在");
		}
		//判断秒杀商品的库存
		if(seckillGoods.getStockCount()<=0){
			throw new RuntimeException("商品已抢购一空");
		}
		//扣减商品库存
		seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
		//放回缓存
		redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods);

		//重新判断商品库存,是否被秒光
		if(seckillGoods.getStockCount()==0){
			//被秒光,1.同步到数据库,2直接删除库存中的商品
			tbSeckillGoodsMapper.updateByPrimaryKey(seckillGoods);
			redisTemplate.boundHashOps("seckillGoods").delete(seckillId);

		}
		//保存订单到redis中,
		long orderId  = idWorker.nextId();
		TbSeckillOrder tbSeckillOrder = new TbSeckillOrder();
		tbSeckillOrder.setId(orderId);
		tbSeckillOrder.setCreateTime(new Date());
		tbSeckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
		tbSeckillOrder.setSeckillId(seckillGoods.getId());
		tbSeckillOrder.setSellerId(seckillGoods.getSellerId());
		tbSeckillOrder.setUserId(userId);
		tbSeckillOrder.setStatus("1");
		redisTemplate.boundHashOps("seckillOrder").put(userId, tbSeckillOrder);

	}


	@Override
	public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);


		return seckillOrder;
	}
}
