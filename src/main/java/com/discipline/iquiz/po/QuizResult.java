package com.discipline.iquiz.po;

import lombok.Data;

@Data
public class QuizResult {
    private String id;
    private String qid;
    private String uid;
    private String scQuestionIds;
    private String mcQuestionIds;
    private String bfQuestionIds;
    private String subQuestionIds;

    private String scAnswerIds;
    private String mcAnswerIds;
    private String bfAnswer;
    private String subAnswer;

    private String subPerScore;

    private double scScore=0;
    private double mcScore=0;
    private double bfScore=0;
    private double subScore=0;
    private double perPointScore;

    private int state;
    private String obBriefStatus;
}
