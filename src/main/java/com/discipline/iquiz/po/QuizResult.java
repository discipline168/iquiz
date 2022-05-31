package com.discipline.iquiz.po;

import lombok.Data;

@Data
public class QuizResult {
    private String id;
    private String qid;
    private String scQuestionIds;
    private String mcQuestionIds;
    private String bfQuestionIds;
    private String subQuestionIds;

    private String scAnswerIds;
    private String mcAnswerIds;
    private String bfAnswer;
    private String subAnswer;

    private float ob_score;
    private float sub_score;
    private int state;
}
