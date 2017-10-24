package com.ntech.dao;

import com.ntech.model.Log;
import com.ntech.model.LogExample;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LogMapper {
    long countByExample(LogExample example);

    int deleteByExample(LogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Log record);

    int insertSelective(Log record);

    List<Log> selectByExample(LogExample example);

    Log selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Log record, @Param("example") LogExample example);

    int updateByExample(@Param("record") Log record, @Param("example") LogExample example);

    int updateByPrimaryKeySelective(Log record);

    int updateByPrimaryKey(Log record);

    List<Log> findPage(@Param("limit") int limit,@Param("offset") int offset);

    List<Log> findWithLimit(@Param("name")String name,@Param("limit") int limit,@Param("offset")int offset);

    List<Log> findWithConditions(@Param("name")String name, @Param("limit") int limit,
                                 @Param("offset")int offset, @Param("type")String type,@Param("start")String start,@Param("end")String end);
    long findCount(@Param("name")String name, @Param("limit") int limit,
                   @Param("offset")int offset, @Param("type")String type,@Param("start")String start,@Param("end")String end);
}