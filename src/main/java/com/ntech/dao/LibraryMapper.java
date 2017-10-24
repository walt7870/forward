package com.ntech.dao;

import com.ntech.model.LibraryExample;
import com.ntech.model.LibraryKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LibraryMapper {
    long countByExample(LibraryExample example);

    int deleteByExample(LibraryExample example);

    int deleteByPrimaryKey(LibraryKey key);

    int insert(LibraryKey record);

    int insertSelective(LibraryKey record);

    List<LibraryKey> selectByExample(LibraryExample example);

    int updateByExampleSelective(@Param("record") LibraryKey record, @Param("example") LibraryExample example);

    int updateByExample(@Param("record") LibraryKey record, @Param("example") LibraryExample example);
}