package com.discipline.iquiz.vo;

import com.discipline.iquiz.po.Option;
import lombok.Data;
import java.util.List;

@Data
public class QuestionVo {
    private String id;
    private String content;
    private int type;
    private int point;

    private List<Option> options;
    private List<Option> answer;


    public QuestionVo(String id,String content,int type,int point){
        this.id=id;
        this.content=content;
        this.type=type;
        this.point=point;
    }
}
