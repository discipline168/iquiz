package com.discipline.iquiz.mapper;

import com.discipline.iquiz.po.Quiz;
import com.discipline.iquiz.po.QuizResult;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

public interface QuizResultMapper {

    @Select("SELECT * FROM `quiz_result` WHERE id = #{id} AND uid = #{uid}")
    QuizResult getQuizResultByIdAndUid(@Param("id")String id,@Param("uid")String uid);

    @Select("SELECT * FROM `quiz_result` WHERE id = #{id}")
    QuizResult getQuizResultById(@Param("id")String id);

    @Select("SELECT * FROM `quiz_result` WHERE qid = #{qid} AND uid = #{uid}")
    QuizResult getQuizResultByQidAndUid(@Param("qid")String qid,@Param("uid")String uid);

    @Update("UPDATE `quiz_result` SET sc_answer_ids = #{scAids} ,mc_answer_ids = #{mcAids} ," +
            "bf_answer = #{bfAnswer} ,sub_answer = #{subAnswer} ,sc_score = #{scScore}, mc_score = #{mcScore}, bf_score = #{bfScore}, " +
            "state = #{state}, ob_brief_status = #{obBriefStatus} WHERE id = #{id} AND uid = #{uid}")
    int quizHandIn(@Param("id")String id, @Param("scAids")String scAids, @Param("mcAids")String mcAids,
                   @Param("bfAnswer")String bfAnswer, @Param("subAnswer") String subAnswer,
                   @Param("scScore") BigDecimal scScore,@Param("mcScore") BigDecimal mcScore,
                   @Param("bfScore") BigDecimal bfScore, @Param("state")int state, @Param("obBriefStatus")String obBriefStatus,
                   @Param("uid")String uid);


    @Update("UPDATE `quiz_result` SET sub_per_score = #{subPerScore},sub_score = #{subScore}, state = 2 WHERE id = #{id}")
    int mark(@Param("subPerScore")String subPerScore,@Param("subScore")double subScore,@Param("id")String id);

    @Select("SELECT id FROM `quiz_result` WHERE qid = #{qid} ORDER BY(sc_score+mc_score+bf_score+sub_score) DESC")
    List<String> getRankIdsByQid(@Param("qid")String qid);

    @Insert("INSERT INTO `quiz_result` (id,qid,uid,sc_question_ids,mc_question_ids,bf_question_ids,sub_question_ids,per_point_score) " +
            "VALUES(#{id}, #{qid}, #{uid}, #{scQuestionIds}, #{mcQuestionIds}, #{bfQuestionIds}, #{subQuestionIds}, #{perPointScore})")
    int addQuizResult(@Param("id")String id,@Param("qid")String qid,@Param("uid")String uid,
                      @Param("scQuestionIds")String scQuestionIds,@Param("mcQuestionIds")String mcQuestionIds,
                      @Param("bfQuestionIds")String bfQuestionIds,@Param("subQuestionIds")String subQuestionIds,
                      @Param("perPointScore")double perPointScore);

    @Select("SELECT * FROM `quiz_result` WHERE uid = #{uid} ")
    List<QuizResult> getQuizResultsByUid(@Param("uid")String uid);


}
