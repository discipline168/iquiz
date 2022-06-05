package com.discipline.iquiz.controller;

import com.discipline.iquiz.jwt.util.JWTUtil;
import com.discipline.iquiz.po.ClassRoom;
import com.discipline.iquiz.po.ClassRoomUser;
import com.discipline.iquiz.service.ClassRoomUserService;
import com.discipline.iquiz.service.impl.ClassRoomServiceImpl;
import com.discipline.iquiz.vo.ClassRoomVo;
import com.discipline.iquiz.vo.JsonData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/class")
public class ClassRoomController {

    @Resource
    ObjectMapper objectMapper;
    @Resource
    ClassRoomServiceImpl classRoomServiceImpl;
    @Resource
    ClassRoomUserService classRoomUserServiceImpl;

    /**
     * 教师-新增课堂信息
     **/
    @RequiresRoles("teacher")
    @PostMapping("/add")
    @ResponseBody
    public String add(String name,String cover) throws Exception {
        /*String token= SecurityUtils.getSubject().getPrincipal().toString();
        String id = JWTUtil.getInfoByToken(token).get("id").asString();*/
        String id = classRoomServiceImpl.addClassRoom(name, cover);
        if(!StringUtils.isNullOrEmpty(id))
            return objectMapper.writeValueAsString(JsonData.success("添加班级成功",id));
        return objectMapper.writeValueAsString(JsonData.fail("添加班级失败"));
    }


    @RequiresRoles("teacher")
    @GetMapping("/tlist")
    @ResponseBody
    public String tlist() throws Exception {
        List<ClassRoom> rooms = classRoomServiceImpl.getTClassRooms();
        if(rooms!=null)
            return objectMapper.writeValueAsString(JsonData.success(null,rooms));
        return objectMapper.writeValueAsString(JsonData.fail("获取教师课堂列表信息失败"));
    }

    @RequiresRoles("student")
    @GetMapping("/slist")
    @ResponseBody
    public String slist() throws Exception {
        List<ClassRoom> rooms = classRoomServiceImpl.getSClassRooms();
        if(rooms!=null)
            return objectMapper.writeValueAsString(JsonData.success(null,rooms));
        return objectMapper.writeValueAsString(JsonData.fail("获取学生课堂列表信息失败"));
    }


    @RequiresAuthentication
    @GetMapping("/info/{id}")
    @ResponseBody
    public String info(@PathVariable("id") String id) throws Exception {

        ClassRoomVo classRoomInfo = classRoomServiceImpl.getClassRoomInfo(id);

        if(classRoomInfo!=null)
            return objectMapper.writeValueAsString(JsonData.success("获取课堂信息成功",classRoomInfo));
        return objectMapper.writeValueAsString(JsonData.fail("不存在该课堂信息"));
    }


    /**
     * 学生-加入课堂
     **/
    @RequiresRoles("student")
    @PostMapping("/join")
    @ResponseBody
    public String join(ClassRoomUser classRoomUser) throws Exception {
        int result = classRoomUserServiceImpl.joinClassRoom(classRoomUser);
        if(result>0){
            //返回对应的课堂信息
            ClassRoomVo classRoomInfo = classRoomServiceImpl.getClassRoomInfo(classRoomUser.getCid());
            return objectMapper.writeValueAsString(JsonData.success("加入课堂成功",classRoomInfo));
        }else if(result == -1)
            return objectMapper.writeValueAsString(JsonData.fail("已加入此课堂，请勿重复操作"));
        return objectMapper.writeValueAsString(JsonData.fail("加入课堂失败"));
    }




}
