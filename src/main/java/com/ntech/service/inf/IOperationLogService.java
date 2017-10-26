package com.ntech.service.inf;

import com.ntech.model.OperationLog;
import com.ntech.model.OperationLog;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hasee on 2017/10/25.
 */

public interface IOperationLogService {
    //根据姓名查询日志
    public List<OperationLog> findByName(String name);
    //添加日志
    public void add(OperationLog info);
    List<OperationLog> findAll();

    long totalCount();
    List<OperationLog> findPage(int limit,int offset);

    public List<OperationLog> findByNameWithLimit(String name,int limit,int offset);
    public List<OperationLog> findWithConditions(String name, int limit, int offset,String type, String start,String end);

    public long findCount(String name, int limit, int offset,String type, String start,String end);
}
