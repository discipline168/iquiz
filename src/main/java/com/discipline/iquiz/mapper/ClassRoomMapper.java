package com.discipline.iquiz.mapper;

import com.discipline.iquiz.po.ClassRoom;
import com.discipline.iquiz.po.ClassRoomUser;
import com.discipline.iquiz.vo.ClassRoomVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.sql.SQLException;
import java.util.List;

public interface ClassRoomMapper {

    @Insert("INSERT INTO `classroom` (id,name,tid) VALUES(#{id},#{name},#{tid})")
    int addClassRoom(@Param("id") String id,@Param("name") String name,@Param("tid") String tid)
            throws SQLException;

    @Select("SELECT * FROM `classroom` WHERE tid= #{tid} AND del_flag = 0")
    List<ClassRoom> getClassRoomsByTid(@Param("tid") String tid);

    @Select("SELECT `classroom`.* FROM `classroom_user`,`classroom` WHERE `classroom_user`.uid = #{sid} " +
            "AND `classroom_user`.cid = `classroom`.id AND `classroom_user`.del_flag = 0 AND `classroom`.del_flag = 0")
    List<ClassRoom> getClassRoomsBySid(@Param("sid")String sid);

    ClassRoomVo getClassRoomById(@Param("id") String id);

    @Select("SELECT * FROM `classroom_user` WHERE cid = #{cid} ")
    List<ClassRoomUser> getClassRoomUsersByCid(@Param("cid")String cid);

}
