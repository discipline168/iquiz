package com.discipline.iquiz.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonData {
    /**
     * 状态码
     * -3 未携带token或token无效
     * -2 token失效
     * -1 代表请求失败
     * 0 无请求权限
     * 1 代表请求成功
     **/
    private int code;
    /**
     * 消息
     **/
    private String msg;
    /**
     * 数据
     **/
    private Object data;


    public static JsonData tokenNull(){
        return new JsonData(-3,"无效的token",null);
    }
    public static JsonData tokenExpired(){
        return new JsonData(-2,"token已过期",null);
    }
    public static JsonData fail(String msg){
        return new JsonData(-1,msg,null);
    }

    public static JsonData authorizeWrong(){
        return new JsonData(0,"无此访问权限",null);
    }

    public static JsonData success(String msg,Object object){
        return new JsonData(1,msg,object);
    }
}
