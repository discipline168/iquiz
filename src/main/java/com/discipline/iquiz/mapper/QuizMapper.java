package com.discipline.iquiz.mapper;

import com.discipline.iquiz.po.Quiz;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.Date;
import java.util.List;

public interface QuizMapper {
    @Select("SELECT * FROM `quiz` WHERE cid = #{cid} AND del_flag = 0")
    List<Quiz> getQuizzesByCid(String cid);

    @Select("SELECT * FROM `quiz` WHERE DATE_FORMAT(DATE_ADD(time,INTERVAL duration MINUTE),'%Y-%m-%d %H:%i:%S') >= DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%S') " +
            "AND cid = #{cid} AND del_flag = 0")
    List<Quiz> getToBeCompletedQuizzesByCid(@Param("cid")String cid);

    @Update("UPDATE `quiz` SET del_flag = 1 WHERE id = #{id} AND tid = #{tid}")
    int deleteQuizzByIdAndTid(@Param("id") String id, @Param("tid") String tid);

    @Insert("INSERT INTO `quiz` VALUES(#{id}, #{name}, #{tid}, #{cid}, #{mode}, #{randomNum}, #{qbank_id}, #{is_preview}, " +
            "#{is_random_option}, #{duration}, #{time}, #{question_ids}, 0)")
    int addQuiz(@Param("id") String id, @Param("name")String name, @Param("tid")String tid,
                @Param("cid")String cid, @Param("mode")int mode, @Param("randomNum")String randomNum,
                @Param("qbank_id")String qbankId,@Param("is_preview")int isPreview,
                @Param("is_random_option")int isRandomOption,@Param("duration")int duration,
                @Param("time")Date time,@Param("question_ids")String questionIds);

    @Select("SELECT * FROM `quiz` WHERE id = #{id} AND tid = #{tid} AND del_flag = 0")
    Quiz getQuizByIdAndTid(@Param("id")String id,@Param("tid")String tid);

    //todo 方法冗余待优化
    @Select("SELECT * FROM `quiz` WHERE id = #{id}")
    Quiz getQuizById(@Param("id")String id);

}
