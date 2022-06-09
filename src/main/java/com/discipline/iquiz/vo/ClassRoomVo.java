package com.discipline.iquiz.vo;

import com.discipline.iquiz.po.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ClassRoomVo {
    private String id;
    private String name;
    private String cover;
    private UserVo teacher;

    private List<Quiz>quizzes;
    private List<Qbank> qbanks;
    private List<ClassRoomUser> students;

    public ClassRoomVo(ClassRoom room, User user){
        this.id=room.getId();
        this.name=room.getName();
        this.cover=room.getCover();
        this.teacher=new UserVo(user.getId(),user.getUsername(),user.getRole(),user.getAvatar());
    }


}
