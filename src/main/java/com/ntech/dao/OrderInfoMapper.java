package com.ntech.dao;

import com.ntech.model.OrderInfo;
import com.ntech.model.OrderInfoExample;
import com.ntech.model.OrderInfoKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderInfoMapper {
    long countByExample(OrderInfoExample example);

    int deleteByExample(OrderInfoExample example);

    int deleteByPrimaryKey(OrderInfoKey key);

    int insert(OrderInfo record);

    int insertSelective(OrderInfo record);

    List<OrderInfo> selectByExample(OrderInfoExample example);

    OrderInfo selectByPrimaryKey(OrderInfoKey key);

    int updateByExampleSelective(@Param("record") OrderInfo record, @Param("example") OrderInfoExample example);

    int updateByExample(@Param("record") OrderInfo record, @Param("example") OrderInfoExample example);

    int updateByPrimaryKeySelective(OrderInfo record);

    int updateByPrimaryKey(OrderInfo record);
}