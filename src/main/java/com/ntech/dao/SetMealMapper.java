package com.ntech.dao;

        import com.ntech.model.SetMeal;
        import com.ntech.model.SetMealExample;
        import java.util.List;
        import org.apache.ibatis.annotations.Param;

public interface SetMealMapper {
    long countByExample(SetMealExample example);

    int deleteByExample(SetMealExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(SetMeal record);

    int insertSelective(SetMeal record);

    List<SetMeal> selectByExample(SetMealExample example);

    SetMeal selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") SetMeal record, @Param("example") SetMealExample example);

    int updateByExample(@Param("record") SetMeal record, @Param("example") SetMealExample example);

    int updateByPrimaryKeySelective(SetMeal record);

    int updateByPrimaryKey(SetMeal record);

    List<SetMeal> findPage(@Param("limit") int limit,@Param("offset") int offsett);

    List<SetMeal> findConditions(@Param("offset") int offset, @Param("limit") int limit,
                                 @Param("name") String name, @Param("type") String type);

    long findCount(@Param("name") String name, @Param("type") String type);
}