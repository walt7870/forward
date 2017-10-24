package com.ntech.dao;

import com.ntech.model.Customer;
import com.ntech.model.CustomerExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CustomerMapper {
    long countByExample(CustomerExample example);

    int deleteByExample(CustomerExample example);

    int deleteByPrimaryKey(String name);

    int insert(Customer record);

    int insertSelective(Customer record);

    List<Customer> selectByExample(CustomerExample example);

    Customer selectByPrimaryKey(String name);

    int updateByExampleSelective(@Param("record") Customer record, @Param("example") CustomerExample example);

    int updateByExample(@Param("record") Customer record, @Param("example") CustomerExample example);

    int updateByPrimaryKeySelective(Customer record);

    int updateByPrimaryKey(Customer record);

    List<Customer> findPage(@Param("limit") int limit,@Param("offset") int offset);

    List<Customer> findLikeName(String name);
}