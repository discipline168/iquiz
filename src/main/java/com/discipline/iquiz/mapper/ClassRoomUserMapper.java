package com.discipline.iquiz.mapper;

import com.discipline.iquiz.po.ClassRoom;
import com.discipline.iquiz.po.ClassRoomUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClassRoomUserMapper {
    @Insert("INSERT INTO `classroom_user` (id,cid,uid,sname,sno) VALUES(#{id}, #{cid}, #{uid}, #{sname}, #{sno})")
    int joinClassRoom(@Param("id")String id,@Param("cid")String cid,@Param("uid")String uid,
                      @Param("sname")String sname, @Param("sno")String sno);

    @Select("SELECT cid FROM `classroom_user` WHERE uid = #{uid} AND del_flag = 0")
    List<String> getCidsByUid(@Param("uid")String uid);

    @Select("SELECT * FROM `classroom_user` WHERE cid = #{cid} AND del_flag = 0")
    List<ClassRoomUser> getStudentIdsByCid(@Param("cid")String cid);
}
