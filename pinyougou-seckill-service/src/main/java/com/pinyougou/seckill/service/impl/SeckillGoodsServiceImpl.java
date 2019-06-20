package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;

import com.pinyougou.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;


import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	@Override
	public List<TbSeckillGoods> findList() {
		//首先先从缓存中读取,
		List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();

		if(seckillGoods==null||seckillGoods.size()<=0){//缓存中没有从数据库查询

			System.out.println("从数据库读取秒杀商品");
			TbSeckillGoodsExample tbSeckillGoodsExample=new TbSeckillGoodsExample();
			Criteria criteria = tbSeckillGoodsExample.createCriteria();
			criteria.andStatusEqualTo("1");//审核通过
			criteria.andStockCountGreaterThan(0);//库存大于0
			criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间小于当前时间
			criteria.andEndTimeGreaterThan(new Date());//结束时间要大于当前时间
			seckillGoods = seckillGoodsMapper.selectByExample(tbSeckillGoodsExample);
			//将商品存入到缓存中
			System.out.println("存入缓存");
			for (TbSeckillGoods seckillGood : seckillGoods) {
				redisTemplate.boundHashOps("seckillGoods").put(seckillGood.getId(), seckillGood);

			}

		}else{
			System.out.println("从缓存中读取");
		}


		return seckillGoods;
	}

	//商品详情页
	@Override
	public TbSeckillGoods findOneFromRedis(Long id) {
		//先前查询秒杀时,已将商品存入redis,这里直接从缓存中读取
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);

		return seckillGoods;
	}

	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillGoods> findAll() {
		return seckillGoodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillGoods> page=   (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillGoods seckillGoods) {
		seckillGoodsMapper.insert(seckillGoods);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillGoods seckillGoods){
		seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillGoods findOne(Long id){
		return seckillGoodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillGoodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillGoodsExample example=new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillGoods!=null){			
						if(seckillGoods.getTitle()!=null && seckillGoods.getTitle().length()>0){
				criteria.andTitleLike("%"+seckillGoods.getTitle()+"%");
			}
			if(seckillGoods.getSmallPic()!=null && seckillGoods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+seckillGoods.getSmallPic()+"%");
			}
			if(seckillGoods.getSellerId()!=null && seckillGoods.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillGoods.getSellerId()+"%");
			}
			if(seckillGoods.getStatus()!=null && seckillGoods.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillGoods.getStatus()+"%");
			}
			if(seckillGoods.getIntroduction()!=null && seckillGoods.getIntroduction().length()>0){
				criteria.andIntroductionLike("%"+seckillGoods.getIntroduction()+"%");
			}
	
		}
		
		Page<TbSeckillGoods> page= (Page<TbSeckillGoods>)seckillGoodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
