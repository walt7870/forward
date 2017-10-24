package com.ntech.service.inf;

import com.ntech.model.OrderInfo;

public interface IOrderInfoService {
     //创建订单
     boolean  createOrder(OrderInfo orderInfo);
     //删除订单
     boolean deleteorder(String orderId);
     //修改订单
     boolean modifyOrder(OrderInfo orderInfo);
     //查询订单
     OrderInfo findOrderById(String orderId);

}
