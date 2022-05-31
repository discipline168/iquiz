package com.discipline.iquiz.service;

import com.discipline.iquiz.po.ClassRoom;
import com.discipline.iquiz.vo.ClassRoomVo;

import java.sql.SQLException;
import java.util.List;

public interface ClassRoomService {
    int addClassRoom(String name,String tid) throws SQLException;

    List<ClassRoom> getTClassRooms();

    List<ClassRoom> getSClassRooms();

    ClassRoomVo getClassRoomInfo(String id);
}
