package com.ntech.service.impl;

import com.ntech.dao.OperationLogMapper;
import com.ntech.model.OperationLog;
import com.ntech.model.OperationLogExample;
import com.ntech.service.inf.IOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hasee on 2017/10/25.
 */
@Service
public class OperationLogService implements IOperationLogService {
    @Autowired
    private OperationLogMapper OperationLogMapper;


    @Override
    public List<OperationLog> findByName(String name) {
        return null;
    }

    @Transactional
    public void add(OperationLog OperationLog) {
        OperationLogMapper.insert(OperationLog);

    }

    @Override
    public List<OperationLog> findAll() {
        return null;
    }

    @Override
    public long totalCount() {
        return 0;
    }

    @Override
    public List<OperationLog> findPage(int limit, int offset) {
        return null;
    }

    @Override
    public List<OperationLog> findByNameWithLimit(String name, int limit, int offset) {
        return null;
    }

    @Override
    public List<OperationLog> findWithConditions(String name, int limit, int offset, String type, String start, String end) {
        return null;
    }

    @Override
    public long findCount(String name, int limit, int offset, String type, String start, String end) {
        return 0;
    }

}
