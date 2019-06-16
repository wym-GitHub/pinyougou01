package com.pinyougou.sellergoods.controller;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;

import com.pinyougou.pojo.TbItem;

import entity.Goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */

	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */

	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@Autowired
	private Destination queueSolrDeleteDestination;
	@Autowired
	private Destination topicPageDeleteDestination;
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});


//			itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){

		return goodsService.findPage(goods, page, rows);		
	}
		@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination queueSolrDestination;
	@Autowired
	private Destination topicPageDestination;
//
		@RequestMapping("updateStatus")
	public Result updateStatus(Long[] ids,String status){


			try {
				for (Long id : ids) {
                    goodsService.updateStatus(ids, status);

                }
                if(status.equals("1")){

					List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, status);

					if(itemList.size()>0){
						String s = JSON.toJSONString(itemList);
						jmsTemplate.send(queueSolrDestination, new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {


								return session.createTextMessage(s);
							}
						});

						System.out.println("同步索引库");


					}else{
						System.out.println("没有明细数据");
					}
					//静态页面生成
					for(Long goodsId:ids){
						jmsTemplate.send(topicPageDestination, new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								return session.createTextMessage(goodsId+"");
							}
						});
					}


				}
                return new Result(true,"审核成功");
			} catch (Exception e) {

				e.printStackTrace();
				return  new Result(false,"审核失败");
			}

		}




	
}
