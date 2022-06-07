package com.discipline.iquiz.vo;

import com.discipline.iquiz.po.ClassRoomUser;
import com.discipline.iquiz.po.Option;
import com.discipline.iquiz.po.Quiz;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Data
public class QuizResultVo {
    private String id;
    private String uid;
    private Quiz quiz;
    private int state;

    private ClassRoomUser student;

    private Map<String, List<QuestionVo>> questions;

    private List<String> scAnswerIds;
    private List<List<String>> mcAnswerIds;
    private List<String> bfAnswer;
    private List<String> subAnswer;

    /**
     * 单/多选/填空/主观题得分
     **/
    private double scScore=0;
    private double mcScore=0;
    private double bfScore=0;
    private double subScore=0;

    /**
     * 每权值所代表的分值
     **/
    private double perPointScore;

    /**
     * 各道主观题得分
     **/
    private List<Double> subPerScore;

    /**
     * 排名
     **/
    private int rank;
    private int num;

    /**
     * 单/多选/填空/主观题总分
     **/
    private BigDecimal scAllScore;
    private BigDecimal mcAllScore;
    private BigDecimal bfAllScore;
    private BigDecimal subAllScore;

    /**
     *  客观题对错状态简要
     **/
    private String[]obBriefStatus;





}
