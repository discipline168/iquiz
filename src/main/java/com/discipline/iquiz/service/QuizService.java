package com.discipline.iquiz.service;

import com.discipline.iquiz.po.Quiz;

import java.util.List;

public interface QuizService {
    int deleteQuiz(String id);
    String addQuiz(Quiz quiz);
    Quiz getQuizPreview(String id);
    List<Quiz> getBeToCompletedQuizzes();
    int open(String id);

}
