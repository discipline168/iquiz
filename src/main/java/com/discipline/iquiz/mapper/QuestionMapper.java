package com.discipline.iquiz.mapper;

import com.discipline.iquiz.po.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface QuestionMapper {
    @Select("SELECT * FROM `question` WHERE tid = #{tid} AND qbank_id = #{bid} AND del_flag = 0 ORDER BY create_time DESC")
    List<Question> getQuestionsByTidAndBid(@Param("tid")String tid, @Param("bid")String bid);

    @Insert("INSERT INTO `question` (id,content,type,point,option_ids,answer_ids,qbank_id,tid,create_time) " +
            "VALUES(#{id},#{content},#{type},#{point},#{optionIds},#{answerIds},#{qbankId},#{tid},#{time})")
    int addQuestionToQbank(@Param("id")String id,@Param("content")String content,@Param("type")int type,
                              @Param("point")int point,@Param("optionIds")String optionIds,
                              @Param("answerIds")String answerIds,@Param("qbankId")String qbankId,
                              @Param("tid")String tid,@Param("time")Date date);

    @Select("SELECT * FROM `question` WHERE tid = #{tid} AND id = #{id} AND del_flag = 0")
    Question getQuestionByTidAndId(@Param("tid")String tid,@Param("id")String id);

    @Select("SELECT id,content,type,point,option_ids FROM `question` WHERE id = #{id} ")
    Question getQuestionById(@Param("id")String id);
}
