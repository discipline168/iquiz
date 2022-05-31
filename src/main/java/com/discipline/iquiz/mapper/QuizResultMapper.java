package com.discipline.iquiz.mapper;

import com.discipline.iquiz.po.QuizResult;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface QuizResultMapper {

    @Select("SELECT * FROM `quiz_result` WHERE id = #{id} AND uid = #{uid}")
    QuizResult getQuizResultByIdAndUid(@Param("id")String id,@Param("uid")String uid);
}
