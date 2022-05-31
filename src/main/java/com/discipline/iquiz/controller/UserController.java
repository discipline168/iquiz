package com.discipline.iquiz.controller;

import com.discipline.iquiz.service.impl.UserServiceImpl;
import com.discipline.iquiz.vo.JsonData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    ObjectMapper objectMapper;
    @Resource
    UserServiceImpl userServiceImpl;

    @PostMapping("/login")
    @ResponseBody
    public String login(String username,String password, HttpServletResponse response) throws Exception {
        Map<String, String> data = userServiceImpl.jwtLogin(username, password);
        if(data==null)
            return objectMapper.writeValueAsString(JsonData.fail(""));
        response.setHeader("Authorization", data.get("token"));
        return objectMapper.writeValueAsString(JsonData.success("",data));

    }


    @GetMapping("/logout")
    @RequiresAuthentication
    public String logout() throws Exception {
        SecurityUtils.getSubject().logout();
        return objectMapper.writeValueAsString(JsonData.success("登出成功",null));
    }
}
