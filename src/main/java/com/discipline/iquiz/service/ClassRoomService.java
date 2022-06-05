package com.discipline.iquiz.service;

import com.discipline.iquiz.po.ClassRoom;
import com.discipline.iquiz.vo.ClassRoomVo;

import java.sql.SQLException;
import java.util.List;

public interface ClassRoomService {
    String addClassRoom(String name,String cover) throws SQLException;

    List<ClassRoom> getTClassRooms();

    List<ClassRoom> getSClassRooms();

    ClassRoomVo getClassRoomInfo(String id);
}
