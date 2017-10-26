package com.ntech.dao;

import com.ntech.model.OperationLog;
import com.ntech.model.OperationLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OperationLogMapper {
    long countByExample(OperationLogExample example);

    int deleteByExample(OperationLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OperationLog record);

    int insertSelective(OperationLog record);

    List<OperationLog> selectByExample(OperationLogExample example);

    OperationLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OperationLog record, @Param("example") OperationLogExample example);

    int updateByExample(@Param("record") OperationLog record, @Param("example") OperationLogExample example);

    int updateByPrimaryKeySelective(OperationLog record);

    int updateByPrimaryKey(OperationLog record);
}