package com.discipline.iquiz.controller;

import com.discipline.iquiz.po.Question;
import com.discipline.iquiz.po.Quiz;
import com.discipline.iquiz.service.impl.QuestionServiceImpl;
import com.discipline.iquiz.service.impl.QuizServiceImpl;
import com.discipline.iquiz.util.IquizConstant;
import com.discipline.iquiz.util.IquizTool;
import com.discipline.iquiz.vo.JsonData;
import com.discipline.iquiz.vo.QuestionVo;
import com.discipline.iquiz.vo.QuizVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.StringUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quiz")
public class QuizController {
    @Resource
    ObjectMapper objectMapper;
    @Resource
    QuizServiceImpl quizServiceImpl;
    @Resource
    QuestionServiceImpl questionServiceImpl;


    @RequiresRoles("teacher")
    @PostMapping("/delete")
    @ResponseBody
    public String delete(String id) throws Exception {
        int result = quizServiceImpl.deleteQuiz(id);
        if(result!=0)
            return objectMapper.writeValueAsString(JsonData.success("删除考试信息成功",null));
        return objectMapper.writeValueAsString(JsonData.fail("删除考试信息失败"));
    }


    @RequiresRoles("teacher")
    @PostMapping("/add")
    @ResponseBody
    public String add(Quiz quiz) throws Exception {
        String id = quizServiceImpl.addQuiz(quiz);

        if(!StringUtils.isNullOrEmpty(id))
            return objectMapper.writeValueAsString(JsonData.success("新增考试信息成功",id));
        return objectMapper.writeValueAsString(JsonData.fail("新增考试信息失败"));
    }


    @RequiresRoles("teacher")
    @GetMapping("/preview/{id}")
    @ResponseBody
    public String preview(@PathVariable("id") String id) throws Exception {

        Quiz quiz = quizServiceImpl.getQuizPreview(id);

        if(quiz!=null){
            QuizVo quizVo = new QuizVo(quiz.getId(),quiz.getName(),quiz.getCid(),quiz.getMode(),quiz.getRandomNum(),
                    quiz.getQbankId(),quiz.getIsRandomOption(),quiz.getIsPreview(),quiz.getDuration()
                    ,quiz.getTime(),quiz.getStatus());
            List<QuestionVo> questionVos=new ArrayList<>();

            if(quiz.getMode()== IquizConstant.NORMAL_MODE_QUIZ){
                QuestionVo questionVo;
                String[] qids = quiz.getQuestionIds().split(",");

                for(String qid:qids){
                    questionVo = questionServiceImpl.getQuestion(qid);
                    if(questionVo!=null)
                        questionVos.add(questionVo);
                }
                //题目集分类
                Map<String, List<QuestionVo>> map = IquizTool.classifyQues(questionVos);
                quizVo.setQuestions(map);
            }
            return objectMapper.writeValueAsString(JsonData.success(null,quizVo));
        }
        return objectMapper.writeValueAsString(JsonData.fail("预览考试信息失败"));
    }


    @RequiresRoles("student")
    @GetMapping("/tbcQuizzes")
    @ResponseBody
    public String tbcQuizzes() throws Exception {
        List<Quiz> quizzes = quizServiceImpl.getBeToCompletedQuizzes();
        if(quizzes!=null)
            return objectMapper.writeValueAsString(JsonData.success(null,quizzes));
        return objectMapper.writeValueAsString(JsonData.fail("获取待完成考试信息失败"));
    }


    /**
     * 教师-启用考试发放试卷
     **/
    @RequiresRoles("teacher")
    @PostMapping("/open")
    @ResponseBody
    public String open(String id) throws Exception {
        int result = quizServiceImpl.open(id);
        if(result>0)
            return objectMapper.writeValueAsString(JsonData.success("启用考试成功",null));
        return objectMapper.writeValueAsString(JsonData.fail("启用考试失败"));
    }
}
