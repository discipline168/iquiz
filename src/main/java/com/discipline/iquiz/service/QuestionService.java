package com.discipline.iquiz.service;

import com.discipline.iquiz.dto.QuestionDto;
import com.discipline.iquiz.vo.QuestionVo;

import java.util.List;

public interface QuestionService {
    List<QuestionVo> getQbankQuestions(String bid);
    QuestionVo getQuestion(String id);
    String addQuestionToQbank(QuestionDto questionDto);
}
