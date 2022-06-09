package com.discipline.iquiz.dto;

import lombok.Data;

@Data
public class QuestionDto {
    private String id;
    private String content;
    private int type;
    private int point;
    private String knowledge;

    private String qbankId;
    private String[] optionContents;
    private int[] answerIndexes;

}
