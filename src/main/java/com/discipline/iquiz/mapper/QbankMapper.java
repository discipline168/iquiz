package com.discipline.iquiz.mapper;

import com.discipline.iquiz.po.Qbank;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

public interface QbankMapper {
    @Select("SELECT * FROM `qbank` WHERE cid = #{cid} AND tid = #{tid} AND del_flag = 0 ORDER BY create_time DESC")
    List<Qbank> getQbanksByCidAndTid(@Param("cid") String cid,@Param("tid")String tid);

    @Insert("INSERT INTO `qbank` (id,name,tid,cid,create_time) VALUES(#{id},#{name},#{tid}, #{cid}, #{time})")
    int addQbank(@Param("id")String id, @Param("name")String name,
                 @Param("tid")String tid, @Param("cid")String cid, @Param("time")Date time);

    @Update("UPDATE `qbank` SET del_flag = 1 WHERE id = #{id} AND tid = #{tid}")
    int deletQbankByIdAndTid(@Param("id")String id,@Param("tid")String tid);
}
