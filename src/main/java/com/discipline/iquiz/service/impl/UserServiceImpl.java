package com.discipline.iquiz.service.impl;

import com.discipline.iquiz.mapper.UserMapper;
import com.discipline.iquiz.po.User;
import com.discipline.iquiz.service.UserService;
import com.discipline.iquiz.jwt.util.JWTUtil;
import com.discipline.iquiz.vo.UserVo;
import com.mysql.jdbc.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public Map<String,Object> jwtLogin(String username, String password) throws Exception {

        Map<String,Object> map=new HashMap<>();

        //密码md5+salt混淆
        String salt = userMapper.getSaltByUsername(username);
        if(!StringUtils.isNullOrEmpty(salt)){
            String pwd = new Md5Hash(password, salt).toHex();
            User user = userMapper.getUser(username, pwd);

            if(user!=null) {
                map.put("token", JWTUtil.generateToken(user.getId(), pwd, user.getRole()));
                //map.put("role", user.getRole() + "");
                map.put("user",new UserVo(user.getId(),user.getUsername(),user.getRole(),user.getAvatar()));

                return map;
            }
        }

        return null;
    }
}
