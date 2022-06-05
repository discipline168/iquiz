package com.discipline.iquiz.service;

import com.discipline.iquiz.po.Quiz;
import com.discipline.iquiz.po.QuizResult;
import com.discipline.iquiz.vo.QuizPaperVo;
import com.discipline.iquiz.vo.QuizResultVo;

import java.util.List;

public interface QuizResultService {
    QuizPaperVo getQuizPaper(String id);
    int quizHandIn(QuizResult quizResult);
    QuizResultVo getQuizResult(String id);
    List<QuizResultVo> getClassQuizResult(String qid);
    int mark(String subScore,String id);

    List<QuizPaperVo> getBeToCompletedQuizzes();

}
