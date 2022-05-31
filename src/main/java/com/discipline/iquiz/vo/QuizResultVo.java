package com.discipline.iquiz.vo;

import com.discipline.iquiz.po.Quiz;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class QuizResultVo {
    private String id;
    private String uid;
    private Quiz quiz;
    private Map<String, List<QuestionVo>> questions;

    public QuizResultVo(String id,String uid,Quiz quiz){
        this.id=id;
        this.uid=uid;
        this.quiz=quiz;
    }
}
