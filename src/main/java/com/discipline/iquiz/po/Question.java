package com.discipline.iquiz.po;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Question {
    private String id;
    private String content;
    private int type;
    private int point;
    private String optionIds;
    private String answerIds;
    private String qbankId;
    private String tid;


    public Question(String content,int type,int point){

    }
}
