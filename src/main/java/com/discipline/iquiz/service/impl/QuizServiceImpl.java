package com.discipline.iquiz.service.impl;

import com.discipline.iquiz.mapper.ClassRoomUserMapper;
import com.discipline.iquiz.mapper.QuizMapper;
import com.discipline.iquiz.po.Quiz;
import com.discipline.iquiz.service.QuizService;
import com.discipline.iquiz.util.IquizTool;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {
    @Resource
    QuizMapper quizMapper;
    @Resource
    ClassRoomUserMapper classRoomUserMapper;

    /**
     * 逻辑删除考试信息
     * @param id 考试id
     **/
    @Override
    public int deleteQuiz(String id) {
        //todo 已过考试时间的考试信息不能删除
        String userId = IquizTool.getUserId();
        return quizMapper.deleteQuizzByIdAndTid(id,userId);
    }

    /**
     * 添加考试信息
     * @param quiz 考试po
     * @return 考试id
     **/
    @Override
    public String addQuiz(Quiz quiz) {
        String id = IquizTool.generateRandomString("quiz_", 8, false);
        String userId = IquizTool.getUserId();
        int result;
        try {
            result=quizMapper.addQuiz(id,quiz.getName(),userId,quiz.getClassId(),quiz.getMode(),
                    quiz.getRandomNum(),quiz.getQbankId(),quiz.getIsPreview(),quiz.getIsRandomOption(),
                    quiz.getDuration(),quiz.getTime(),quiz.getQuestionIds());
        }catch (Exception e){
            e.printStackTrace();
            id = IquizTool.generateRandomString("quiz_", 8, false);
            result=quizMapper.addQuiz(id,quiz.getName(),userId,quiz.getClassId(),quiz.getMode(),
                    quiz.getRandomNum(),quiz.getQbankId(),quiz.getIsPreview(),quiz.getIsRandomOption(),
                    quiz.getDuration(),quiz.getTime(),quiz.getQuestionIds());
        }
        if(result==1)
            return id;
        return null;
    }

    /**
     * 预览考试信息
     * @param id 考试id
     **/
    @Override
    public Quiz getQuizPreview(String id) {
        String userId = IquizTool.getUserId();
        return quizMapper.getQuizByIdAndTid(id, userId);

    }

    /**
     * 学生-获取待完成的考试信息
     **/
    @Override
    public List<Quiz> getBeToCompletedQuizzes() {
        //获取学生已加入的课堂列表
        String userId = IquizTool.getUserId();
        List<String> cids = classRoomUserMapper.getCidsByUid(userId);

        if(cids.size()>0){
            List<Quiz> quizzes=new ArrayList<>();

            for(String cid: cids)
                quizzes.addAll(quizMapper.getToBeCompletedQuizzesByCid(cid));
            return quizzes;
        }
        return null;

    }
}
