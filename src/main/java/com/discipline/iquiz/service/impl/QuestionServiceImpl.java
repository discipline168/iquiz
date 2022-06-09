package com.discipline.iquiz.service.impl;

import com.discipline.iquiz.dto.QuestionDto;
import com.discipline.iquiz.mapper.OptionMapper;
import com.discipline.iquiz.mapper.QuestionMapper;
import com.discipline.iquiz.po.Option;
import com.discipline.iquiz.po.Question;
import com.discipline.iquiz.service.QuestionService;
import com.discipline.iquiz.util.IquizConstant;
import com.discipline.iquiz.util.IquizTool;
import com.discipline.iquiz.vo.QuestionVo;
import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Resource
    QuestionMapper questionMapper;
    @Resource
    OptionMapper optionMapper;

    /**
     * 获取题库里的所有题目
     * @param bid 题库id
     **/
    @Override
    public List<QuestionVo> getQbankQuestions(String bid) {
        String userId = IquizTool.getUserId();
        //String userId = "666999666";
        List<Question> questions = questionMapper.getQuestionsByTidAndBid(userId, bid);

        if(questions!=null){

            List<QuestionVo>questionVos=new ArrayList<>();
            QuestionVo questionVo;

            for (Question que : questions){
                //questionVo = formatQue(que, userId);
                questionVo = IquizTool.formatQue(que,optionMapper,true);
                questionVos.add(questionVo);
            }
            return questionVos;
        }

        return null;
    }



    /**
     * 根据id获取题目信息
     * @param  id 题目id
     **/
    @Override
    public QuestionVo getQuestion(String id) {
        String userId = IquizTool.getUserId();
        Question que = questionMapper.getQuestionByTidAndId(userId, id);

        if(que!=null)
            //return formatQue(que, userId);
            return IquizTool.formatQue(que,optionMapper,true);
        return null;
    }



    /**
     * 往题库里添加题目
     * @param questionDto 题目vo
     **/
    @Override
    public String addQuestionToQbank(QuestionDto questionDto) {
        int type = questionDto.getType();
        if(!StringUtils.isNullOrEmpty(questionDto.getContent())&&type!=0&&type<5){

            String userId = IquizTool.getUserId();
            //String userId = "666999666";

            //选项id列表
            List<String>oidList;
            //答案id列表
            List<String> aidList;

            String optionIds = null;
            String answerIds = null;


            //选项添加操作
            String[] optionContents = questionDto.getOptionContents();
            if(optionContents!=null){
                //添加选项(当类型为填空题时里面存储则是填空答案内容集)并返回id集合
                oidList=new ArrayList<>();
                aidList=new ArrayList<>();

                for (String str:optionContents){
                    if(StringUtils.isNullOrEmpty(str.trim()))
                        continue;
                    int result;
                    String oid=IquizTool.generateRandomString("op_",10,false);
                    try {
                        result=optionMapper.addOption(oid, str, userId);
                    }catch (Exception e){
                        oid=IquizTool.generateRandomString("op_",10,false);
                        result=optionMapper.addOption(oid, str, userId);
                    }

                    if(result>0)
                        oidList.add(oid);
                }

                //当为选择题时，则从下标数组里获取正确选项id集合
                if(type==IquizConstant.SINGLE_CHOICE_QUESTION
                        ||type==IquizConstant.MULTI_CHOICE_QUESTION){

                    int[] answerIndexes = questionDto.getAnswerIndexes();
                    if(answerIndexes==null)
                        return null;

                    //拼接正确选项id
                    answerIds = "";
                    for (int index:answerIndexes){
                        aidList.add(oidList.get(index));
                        //answerIds+=oidList.get(index)+",";
                    }

                    answerIds=String.join(",",aidList);
                    optionIds=String.join(",",oidList);
                }
                //当为填空题或主观题时，其答案id集合即为先前所添加的选项id集合
                else{
                    answerIds=String.join(",",oidList);
                }
            }

            String qid=IquizTool.generateRandomString("que_",8,false);
            int result;

            try {
                result=questionMapper.addQuestionToQbank(qid,questionDto.getContent(),questionDto.getType(),
                        questionDto.getPoint(),optionIds,answerIds,questionDto.getQbankId(),userId,new Date(),questionDto.getKnowledge());
            }catch (Exception e){
                qid=IquizTool.generateRandomString("que_",8,false);
                result=questionMapper.addQuestionToQbank(qid,questionDto.getContent(),questionDto.getType(),
                        questionDto.getPoint(),optionIds,answerIds,questionDto.getQbankId(),userId,new Date(),questionDto.getKnowledge());
            }

            if(result>0)
                return qid;

        }
        return null;
    }




}
