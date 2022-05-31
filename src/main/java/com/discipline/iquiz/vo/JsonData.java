package com.discipline.iquiz.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonData {
    /**
     * 状态码 -1 代表失败；1 代表成功
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


    public static JsonData fail(String msg){
        return new JsonData(-1,msg,null);
    }
    public static JsonData success(String msg,Object object){
        return new JsonData(1,msg,object);
    }
}
