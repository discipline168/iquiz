package com.discipline.iquiz.vo;

import com.discipline.iquiz.po.ClassRoomUser;
import com.discipline.iquiz.po.Qbank;
import com.discipline.iquiz.po.Quiz;
import lombok.Data;
import java.util.List;

@Data
public class ClassRoomVo {
    private String id;
    private String name;
    private String cover;
    private UserVo teacher;

    private List<Quiz>quizzes;
    private List<Qbank> qbanks;
    private List<ClassRoomUser> students;
}
