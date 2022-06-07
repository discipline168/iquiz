package com.discipline.iquiz.controller;

import com.discipline.iquiz.po.Quiz;
import com.discipline.iquiz.po.QuizResult;
import com.discipline.iquiz.service.impl.QuizResultServiceImpl;
import com.discipline.iquiz.vo.JsonData;
import com.discipline.iquiz.vo.QuizPaperVo;
import com.discipline.iquiz.vo.QuizResultVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("qresult")
public class QuizResultController {
    @Resource
    ObjectMapper objectMapper;
    @Resource
    QuizResultServiceImpl quizResultServiceImpl;

    @RequiresRoles("student")
    @GetMapping("/start/{id}")
    @ResponseBody
    public String start(@PathVariable("id") String id) throws Exception{
        QuizPaperVo quizResult = quizResultServiceImpl.getQuizPaper(id);
        if(quizResult!=null)
            return objectMapper.writeValueAsString(JsonData.success(null,quizResult));
        return objectMapper.writeValueAsString(JsonData.fail("获取考试结果信息失败"));
    }

    @RequiresRoles("student")
    @PostMapping("/handin")
    @ResponseBody
    public String handin(QuizResult quizResult) throws Exception{
        int result = quizResultServiceImpl.quizHandIn(quizResult);
        if(result==-1)
            return objectMapper.writeValueAsString(JsonData.fail("提交参数不完整"));
        else if(result==-2)
            return objectMapper.writeValueAsString(JsonData.fail("系统错误，请稍后再试"));
        else if(result>0)
            return objectMapper.writeValueAsString(JsonData.success(null,null));
        return objectMapper.writeValueAsString(JsonData.fail("提交试卷失败，请稍后再试"));

    }

    @RequiresAuthentication
    @GetMapping("/detail/{id}")
    @ResponseBody
    public String detail(@PathVariable("id")String id) throws Exception{
        QuizResultVo resultVo = quizResultServiceImpl.getQuizResult(id);
        if(resultVo!=null)
            return objectMapper.writeValueAsString(JsonData.success(null,resultVo));
        return objectMapper.writeValueAsString(JsonData.fail("获取考试结果信息失败"));
    }

    @RequiresRoles("teacher")
    @GetMapping("/classOverview")
    @ResponseBody
    public String cqresult(String qid) throws Exception{
        Map<String, Object> map = quizResultServiceImpl.getClassQuizResults(qid);
        if(map!=null)
            return objectMapper.writeValueAsString(JsonData.success(null,map));
        return objectMapper.writeValueAsString(JsonData.fail("获取考试结果信息失败"));
    }

    @RequiresRoles("teacher")
    @PostMapping("/mark")
    @ResponseBody
    public String mark(String subPerScore,String id) throws Exception{
        int result = quizResultServiceImpl.mark(subPerScore, id);
        if(result>0)
            return objectMapper.writeValueAsString(JsonData.success(null,null));
        return objectMapper.writeValueAsString(JsonData.fail("评阅试卷失败"));
    }



    @RequiresRoles("student")
    @GetMapping("/tbcQuizzes")
    @ResponseBody
    public String tbcQuizzes() throws Exception {
        List<QuizPaperVo> quizzes = quizResultServiceImpl.getBeToCompletedQuizzes();
        if(quizzes!=null)
            return objectMapper.writeValueAsString(JsonData.success(null,quizzes));
        return objectMapper.writeValueAsString(JsonData.fail("获取待完成考试信息失败"));
    }

}
