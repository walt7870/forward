package com.ntech.service.impl;

import com.ntech.dao.LogMapper;
import com.ntech.model.Log;
import com.ntech.model.LogExample;
import com.ntech.service.inf.ILogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class LogService implements ILogService {
    @Autowired
    private LogMapper logMapper;

    public List<Log> findByName(String name) {
        LogExample example = new LogExample();
        example.createCriteria().andUserNameEqualTo(name);
        List<Log> logs= logMapper.selectByExample(example);
        return logs;
    }

    @Transactional
    public void add(Log log) {
        logMapper.insert(log);
    }

    public List<Log> findAll() {
        LogExample example=new LogExample();
        example.createCriteria().andIdIsNotNull();
        List<Log> logs=logMapper.selectByExample(example);
        return logs;
    }

    public long totalCount() {
        LogExample example=new LogExample();
        example.createCriteria().andContentIsNotNull();
        return logMapper.countByExample(example);
    }


    public List<Log> findPage(int limit, int offset) {

        return logMapper.findPage(limit,offset);
    }

    public List<Log> findByNameWithLimit(String name, int limit, int offset) {

        return logMapper.findWithLimit(name, limit, offset);
    }

   //按条件查询
    public List<Log> findWithConditions(String name, int limit, int offset,String type, String start, String end) {
        return logMapper.findWithConditions(name,limit,offset,type,start,end);
    }

    //查找总数
    public long findCount(String name, int limit, int offset, String type, String start, String end) {
        return logMapper.findCount(name,limit,offset,type,start,end);
    }

}
