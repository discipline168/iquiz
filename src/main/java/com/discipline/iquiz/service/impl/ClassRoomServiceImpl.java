package com.discipline.iquiz.service.impl;

import com.discipline.iquiz.mapper.ClassRoomMapper;
import com.discipline.iquiz.mapper.QbankMapper;
import com.discipline.iquiz.mapper.QuizMapper;
import com.discipline.iquiz.po.ClassRoom;
import com.discipline.iquiz.po.Quiz;
import com.discipline.iquiz.service.ClassRoomService;
import com.discipline.iquiz.util.IquizTool;
import com.discipline.iquiz.vo.ClassRoomVo;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

@Service
public class ClassRoomServiceImpl implements ClassRoomService {
    @Resource
    ClassRoomMapper classRoomMapper;
    @Resource
    QuizMapper quizMapper;
    @Resource
    QbankMapper qbankMapper;

    @Override
    public String addClassRoom(String name, String cover) throws SQLException {

        String id= IquizTool.generateRandomString("",6,true);
        int result;
        try {
            result=classRoomMapper.addClassRoom(id,name,cover,IquizTool.getUserId());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        if(result>0)
            return id;
        return null;
    }

    @Override
    public List<ClassRoom> getTClassRooms() {
        String tid = IquizTool.getUserId();
        if(tid!=null)
            return classRoomMapper.getClassRoomsByTid(tid);
        return null;
    }

    /**
     * 获取学生用户所加入的课堂列表信息
     **/
    @Override
    public List<ClassRoom> getSClassRooms() {
        String userId = IquizTool.getUserId();
        return classRoomMapper.getClassRoomsBySid(userId);
    }

    @Override
    public ClassRoomVo getClassRoomInfo(String id) {
        //todo 判断登录用户是否与该课堂存在关联
        ClassRoomVo classRoomVo = classRoomMapper.getClassRoomById(id);
        //获取考试列表信息
        if(classRoomVo!=null){
            classRoomVo.setQuizzes(quizMapper.getQuizzesByCid(classRoomVo.getId()));
            String userId = IquizTool.getUserId();
            //若当前登录用户为该班级的任课教师，则额外展示题库列表信息和学生列表信息
            if(classRoomVo.getTeacher().getId().equals(userId)){
                classRoomVo.setQbanks(qbankMapper.getQbanksByCidAndTid(id,userId));
                classRoomVo.setStudents(classRoomMapper.getClassRoomUsersByCid(id));
            }
        }
        return classRoomVo;
    }
}
