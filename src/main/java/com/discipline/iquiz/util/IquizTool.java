package com.discipline.iquiz.util;

import cn.hutool.core.util.RandomUtil;
import com.discipline.iquiz.jwt.util.JWTUtil;
import com.discipline.iquiz.mapper.OptionMapper;
import com.discipline.iquiz.po.Option;
import com.discipline.iquiz.po.Question;
import com.discipline.iquiz.vo.QuestionVo;
import org.apache.shiro.SecurityUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IquizTool {
    @Resource


    /**
     * 随机生成指定前缀和指定长度的字符串（数字+大小写英文字母）
     * @param prefix 前缀字符串
     * @param length 字符串长度
     * @param isCaseUpper 是否大写
     **/
    public static String generateRandomString(String prefix,int length,boolean isCaseUpper){
        return isCaseUpper?
                prefix+RandomUtil.randomString(length).toUpperCase():prefix+RandomUtil.randomString(length);
    }

    /**
     * 返回当前登录用户的id
     **/
    public static String getUserId(){
        String token= SecurityUtils.getSubject().getPrincipal().toString();
        if (token!=null)
            return JWTUtil.getInfoByToken(token).get("id").asString();
        return null;
    }


    /**
     * 对题目集按其题型进行归类划分
     * @param questionVos 题目集
     **/
    public static Map<String, List<QuestionVo>> classifyQues(List<QuestionVo> questionVos){
        if(questionVos!=null){
            Map<String,List<QuestionVo>>map=new HashMap<>();
            List<QuestionVo> scQues = questionVos.stream()
                    .filter((que -> que.getType() == IquizConstant.SINGLE_CHOICE_QUESTION))
                    .collect(Collectors.toList());
            List<QuestionVo> mcQues = questionVos.stream()
                    .filter((que -> que.getType() == IquizConstant.MULTI_CHOICE_QUESTION))
                    .collect(Collectors.toList());
            List<QuestionVo> bfQues = questionVos.stream()
                    .filter((que -> que.getType() == IquizConstant.BLANK_FILLING_QUESTION))
                    .collect(Collectors.toList());
            List<QuestionVo> subQues = questionVos.stream()
                    .filter((que -> que.getType() == IquizConstant.SUBJECTIVE_QUESTION))
                    .collect(Collectors.toList());

            map.put("scQuestions",scQues);
            map.put("mcQuestions",mcQues);
            map.put("bfQuestions",bfQues);
            map.put("subQuestions",subQues);
            return map;
        }
        return null;
    }

    /**
     * 获取客观题的选项/答案集合并转换成vo
     * @param que 题目po
     * @isShowAnswer 是否展示答案
     **/
    public static QuestionVo formatQue(Question que, OptionMapper optionMapper, boolean isShowAnswer){
        QuestionVo questionVo=new QuestionVo(que.getId(),que.getContent(),que.getType(),que.getPoint());
        //判断题型
        //若为客观题
        if(que.getType()!=IquizConstant.SUBJECTIVE_QUESTION){
            //若为选择题
            if(que.getType()==IquizConstant.SINGLE_CHOICE_QUESTION||
                    que.getType()==IquizConstant.MULTI_CHOICE_QUESTION){
                //获取选项集合
                List<Option> options = optionMapper.getOptionsByIdsAndTid(que.getOptionIds().split(","));
                if(options.size()>0)
                    questionVo.setOptions(options);
            }
            //获取正确选项（答案）集合
            if(isShowAnswer){
                List<Option> answer = optionMapper.getOptionsByIdsAndTid(que.getAnswerIds().split(","));
                if(answer.size()>0)
                    questionVo.setAnswer(answer);
            }
        }
        return questionVo;
    }
}
