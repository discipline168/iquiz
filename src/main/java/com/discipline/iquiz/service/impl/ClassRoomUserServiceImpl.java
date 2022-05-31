package com.discipline.iquiz.service.impl;

import com.discipline.iquiz.mapper.ClassRoomMapper;
import com.discipline.iquiz.mapper.ClassRoomUserMapper;
import com.discipline.iquiz.po.ClassRoomUser;
import com.discipline.iquiz.service.ClassRoomUserService;
import com.discipline.iquiz.util.IquizTool;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ClassRoomUserServiceImpl implements ClassRoomUserService {
    @Resource
    ClassRoomUserMapper classRoomUserMapper;

    /**
     * 用户加入课堂
     * @param classRoomUser 班级用户po
     **/
    @Override
    public int joinClassRoom(ClassRoomUser classRoomUser) {
        String userId = IquizTool.getUserId();
        //将主键设置成课堂号+用户id确保唯一性
        classRoomUser.setId(classRoomUser.getCid()+"_"+userId);
        int result;
        try {
            result = classRoomUserMapper.joinClassRoom(classRoomUser.getId(), classRoomUser.getCid(),
                    userId, classRoomUser.getSname(), classRoomUser.getSno());
        }catch (Exception e){
            //主键重复异常
            return -1;
        }
        return result;
    }
}
