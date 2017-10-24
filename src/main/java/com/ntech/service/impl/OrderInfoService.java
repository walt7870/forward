package com.ntech.service.impl;

import com.ntech.dao.OrderInfoMapper;
import com.ntech.model.OrderInfo;
import com.ntech.model.OrderInfoExample;
import com.ntech.service.inf.IOrderInfoService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderInfoService implements IOrderInfoService {

   private static final Logger logger=Logger.getLogger(OrderInfoService.class);

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    //创建订单信息
    public boolean createOrder(OrderInfo orderInfo) {
        logger.info("create order");
        if(orderInfo!=null){
         int result=orderInfoMapper.insertSelective(orderInfo);
         if(result==1){
             logger.info("add order success");
             return true;
         }
             logger.info("add order failed");
             return false;
        }
        return false;
    }

   //根据id删除order
    public boolean deleteorder(String orderId) {
        logger.info("delete order by id start"+orderId);
        OrderInfo orderInfo=findOrderById(orderId);
        OrderInfoExample example=new OrderInfoExample();
        if (orderId!=null){
            example.createCriteria().andOrderIdEqualTo(orderId);
            int result=orderInfoMapper.deleteByExample(example);
            if(result==1){
                logger.info("delete order by id success :"+orderId );
                return  true;
            }
            logger.info("delete order by id failed :"+orderId );
            return  false;
        }
        return false;
    }

    //修改订单
    public boolean modifyOrder(OrderInfo orderInfo) {
       logger.info("modify order start");
       if (orderInfo!=null){
           int result=orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
           if (result==1){
               logger.info("modify order success");
               return  true;
           }
           logger.info("modify order fail");
           return false;
       }
        logger.info("modify order fail");
        return false;
    }

    //根据orderid查询订单信息
    public OrderInfo findOrderById(String oderId) {
        logger.info("find order by orderId"+oderId);
        OrderInfoExample example=new OrderInfoExample();
        example.createCriteria().andOrderIdEqualTo(oderId);
        List<OrderInfo> list=orderInfoMapper.selectByExample(example);
        if(list.size()>0){
            logger.info("find order by orderId success for"+oderId);
            return  list.get(0);
        }
        logger.info("find order by orderId false for"+oderId);
        return  null;
    }

}
