package com.ntech.service.inf;

import com.ntech.model.SetMeal;

import java.util.List;

/**
 * 套餐服务接口
 */
public interface ISetMealService {
    boolean add(SetMeal setMeal);
    boolean delete(String userName);
    List<SetMeal> findAll();
    SetMeal findByName(String name);
    boolean modify(SetMeal setMeal);
    long totalCount();
    List<SetMeal> findByPage(int limit, int i);
    //按条件分页查询
    List<SetMeal> findByConditions(int offset,int limit,String name,String type);
    //按条件查询的总数
    long findCount(String name,String type);
}
