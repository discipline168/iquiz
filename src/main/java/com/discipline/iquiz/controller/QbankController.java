package com.discipline.iquiz.controller;

import com.discipline.iquiz.dto.QuestionDto;
import com.discipline.iquiz.po.Qbank;
import com.discipline.iquiz.service.impl.QbankServiceImpl;
import com.discipline.iquiz.service.impl.QuestionServiceImpl;
import com.discipline.iquiz.util.IquizConstant;
import com.discipline.iquiz.util.IquizTool;
import com.discipline.iquiz.vo.JsonData;
import com.discipline.iquiz.vo.QuestionVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.StringUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/qbank")
public class QbankController {
    @Resource
    ObjectMapper objectMapper;
    @Resource
    QbankServiceImpl qbankServiceImpl;
    @Resource
    QuestionServiceImpl questionServiceImpl;


    @RequiresRoles("teacher")
    @GetMapping("/info/{cid}")
    @ResponseBody
    public String info(@PathVariable("cid") String cid) throws Exception {
        List<Qbank> qbanks = qbankServiceImpl.getClassQbanks(cid);
        if(qbanks!=null)
            return objectMapper.writeValueAsString(JsonData.success("",qbanks));
        return objectMapper.writeValueAsString(JsonData.fail("获取题库信息失败"));
    }

    @RequiresRoles("teacher")
    @PostMapping("/add")
    @ResponseBody
    public String add(String name,String cid) throws Exception {
        String id = qbankServiceImpl.addQbank(name, cid);
        if(!StringUtils.isNullOrEmpty(id))
            return objectMapper.writeValueAsString(JsonData.success("新增题库信息成功",id));
        return objectMapper.writeValueAsString(JsonData.fail("新增题库信息失败"));
    }

    @RequiresRoles("teacher")
    @PostMapping("/delete")
    @ResponseBody
    public String delete(String id) throws Exception {
        int result = qbankServiceImpl.deletQbank(id);
        if(result!=0)
            return objectMapper.writeValueAsString(JsonData.success("删除题库信息成功",null));
        return objectMapper.writeValueAsString(JsonData.fail("删除题库信息失败"));
    }


    @RequiresRoles("teacher")
    @GetMapping("/questions/{bid}")
    @ResponseBody
    public String questions(@PathVariable("bid") String bid) throws Exception {
        List<QuestionVo> questionVos = questionServiceImpl.getQbankQuestions(bid);
        if(questionVos!=null) {
            //统计单/多选/填空/主观题的数量
            int scNum = 0,mcNum = 0,bfNum = 0,subNum = 0;
            for (QuestionVo item:questionVos){
                int type = item.getType();
                if(type== IquizConstant.SINGLE_CHOICE_QUESTION)
                    scNum++;
                else if(type== IquizConstant.MULTI_CHOICE_QUESTION)
                    mcNum++;
                else if(type== IquizConstant.BLANK_FILLING_QUESTION)
                    bfNum++;
                else if (type== IquizConstant.SUBJECTIVE_QUESTION)
                    subNum++;
            }
            Map<String,Object>map=new HashMap<>();
            map.put("questions",questionVos);
            map.put("scNum",scNum);
            map.put("mcNum",mcNum);
            map.put("bfNum",bfNum);
            map.put("subNum",subNum);

            return objectMapper.writeValueAsString(JsonData.success("", map));
        }
        return objectMapper.writeValueAsString(JsonData.fail("获取题库题集失败"));
    }



    @RequiresRoles("teacher")
    @PostMapping("/addque")
    @ResponseBody
    public String addque(QuestionDto questionDto) throws Exception {
        String qid = questionServiceImpl.addQuestionToQbank(questionDto);
        if(!StringUtils.isNullOrEmpty(qid)){
            //获取新增题目信息
            QuestionVo questionVo = questionServiceImpl.getQuestion(qid);
            return objectMapper.writeValueAsString(JsonData.success("",questionVo));
        }
        return objectMapper.writeValueAsString(JsonData.fail("往题库中添加题目信息失败"));
    }
}
