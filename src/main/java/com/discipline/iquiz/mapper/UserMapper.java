package com.discipline.iquiz.mapper;

import com.discipline.iquiz.po.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    //根据用户名获取md5加密盐值
    @Select("SELECT salt FROM `user` WHERE user_name = #{username}")
    String getSaltByUsername(@Param("username")String username);

    //用户名+密码：用于登录
    @Select("SELECT * FROM `user` WHERE user_name = #{username} AND password = #{password}")
    User getUser(@Param("username") String username, @Param("password") String password);

    //获取正确密码：用于token验证
    @Select("SELECT password FROM `user` WHERE id = #{id}")
    String getPwdById(@Param("id")String id);

    //获取正确密码：用于token验证
    @Select("SELECT password FROM `user` WHERE id = #{id}")
    String getRoleById(@Param("id")String id);
}
