package com.discipline.iquiz.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class QuizVo {
    private String id;
    private String name;
    private String classId;
    private int mode;
    private String randomNum;
    private String qbankId;
    private int isRandomOption;
    private int isPreview;
    private int duration;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date time;
    private int status;

    private Map<String,List<QuestionVo>> questions;

    public QuizVo(String id, String name, String classId, int mode, String randomNum,
                  String qbankId, int isRandomOption, int isPreview, int duration, Date time,int status) {
        this.id=id;
        this.name=name;
        this.classId=classId;
        this.mode=mode;
        this.randomNum=randomNum;
        this.qbankId=qbankId;
        this.isRandomOption=isRandomOption;
        this.isPreview=isPreview;
        this.duration=duration;
        this.time=time;
        this.status=status;
    }
}
