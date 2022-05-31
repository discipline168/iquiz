package com.discipline.iquiz.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.discipline.iquiz.mapper.OptionMapper;
import com.discipline.iquiz.mapper.QuestionMapper;
import com.discipline.iquiz.mapper.QuizMapper;
import com.discipline.iquiz.mapper.QuizResultMapper;
import com.discipline.iquiz.po.Question;
import com.discipline.iquiz.po.Quiz;
import com.discipline.iquiz.po.QuizResult;
import com.discipline.iquiz.service.QuizResultService;
import com.discipline.iquiz.util.IquizTool;
import com.discipline.iquiz.vo.QuestionVo;
import com.discipline.iquiz.vo.QuizResultVo;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class QuizResultServiceImpl implements QuizResultService {
    @Resource
    QuizResultMapper quizResultMapper;
    @Resource
    QuizMapper quizMapper;
    @Resource
    QuestionMapper questionMapper;
    @Resource
    OptionMapper optionMapper;

    /**
     * 学生-开始考试获取考试信息
     **/
    @Override
    public QuizResultVo getQuizResult(String id) {
        String userId = IquizTool.getUserId();
        QuizResult quizResult = quizResultMapper.getQuizResultByIdAndUid(id, userId);

        if(quizResult!=null){
            //todo 考试时间判断，未在考试时间范围内不予返回结果

            QuizResultVo quizResultVo = new QuizResultVo(quizResult.getId(),userId,
                    //获取考试信息
                    quizMapper.getQuizById(quizResult.getQid()));

            String[] questionIds = ArrayUtil.addAll(
                    quizResult.getScQuestionIds().split(","),
                    quizResult.getMcQuestionIds().split(","),
                    quizResult.getBfQuestionIds().split(","),
                    quizResult.getSubQuestionIds().split(",")
            );

            ArrayList<QuestionVo> questionVos = new ArrayList<>();
            Question que;

            for(String qid:questionIds){
                que = questionMapper.getQuestionById(qid);
                if(que!=null) {
                    QuestionVo questionVo = IquizTool.formatQue(que, optionMapper, false);
                    questionVos.add(questionVo);
                }
            }
            Map<String, List<QuestionVo>> map = IquizTool.classifyQues(questionVos);
            quizResultVo.setQuestions(map);
            return quizResultVo;
        }
        return null;
    }
}
