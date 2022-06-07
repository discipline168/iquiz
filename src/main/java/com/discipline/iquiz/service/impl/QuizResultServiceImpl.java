package com.discipline.iquiz.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import com.discipline.iquiz.mapper.*;
import com.discipline.iquiz.po.*;
import com.discipline.iquiz.service.QuizResultService;
import com.discipline.iquiz.util.IquizConstant;
import com.discipline.iquiz.util.IquizTool;
import com.discipline.iquiz.vo.QuestionVo;
import com.discipline.iquiz.vo.QuizPaperVo;
import com.discipline.iquiz.vo.QuizResultVo;
import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

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
    @Resource
    ClassRoomUserMapper classRoomUserMapper;

    /**
     * 学生-开始考试获取考试信息
     **/
    @Override
    public QuizPaperVo getQuizPaper(String id) {
        String userId = IquizTool.getUserId();
        QuizResult quizResult = quizResultMapper.getQuizResultByIdAndUid(id, userId);

        if(quizResult!=null){
            //todo 考试时间判断，未在考试时间范围内不予返回结果

            QuizPaperVo quizPaperVo = new QuizPaperVo(quizResult.getId(),userId,
                    //获取考试信息
                    quizMapper.getQuizById(quizResult.getQid()));

            String[] questionIds = ArrayUtil.addAll(
                    quizResult.getScQuestionIds()==null?null:quizResult.getScQuestionIds().split(","),
                    quizResult.getMcQuestionIds()==null?null:quizResult.getMcQuestionIds().split(","),
                    quizResult.getBfQuestionIds()==null?null:quizResult.getBfQuestionIds().split(","),
                    quizResult.getSubQuestionIds()==null?null:quizResult.getSubQuestionIds().split(",")
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
            quizPaperVo.setQuestions(map);
            return quizPaperVo;
        }
        return null;
    }




    /**
     * 学生-提交答题信息并自动对其客观题进行评分
     * @param  quizResult 试卷po
     **/
    @Override
    public int quizHandIn(QuizResult quizResult) {
        //获取试卷原信息
        if(!StringUtils.isNullOrEmpty(quizResult.getId())){
            String userId = IquizTool.getUserId();
            QuizResult origin = quizResultMapper.getQuizResultByIdAndUid(quizResult.getId(), userId);
            //todo 如果state不为0则不让提交，时间过了也不予提交
            //单/多选/填空题目/总权值、获得的权值
            float scPoints=0,mcPoints=0,bfPoints=0,sumPoints=0,scGet=0,mcGet=0,bfGet=0;

            //获取答案信息
            Quiz quiz = quizMapper.getQuizById(origin.getQid());

            List<String>scBriefStatus,mcBriefStatus,bfBriefStatus,obBriefStatus=new ArrayList<>();

            int state=2;


            //单选题
            if(!StringUtils.isNullOrEmpty(origin.getScQuestionIds())){
                String[] scQids = origin.getScQuestionIds().split(",");
                String[] scPickIds = quizResult.getScAnswerIds().split(",");

                if(scQids.length!=scPickIds.length)
                    return -1;
                //单选题对错情况简要
                scBriefStatus = new ArrayList<>();

                Question question;
                System.out.println("----------单选题判断----------");
                for(int i=0;i<scQids.length;i++){
                    //获取单选题正确答案
                    question = questionMapper.getQuestionById(scQids[i]);
                    if(question==null)
                        return -2;
                    System.out.println(scQids[i]+"-题目权值:"+question.getPoint());
                    System.out.println(scQids[i]+"-正确答案:"+question.getAnswerIds());
                    System.out.println(scQids[i]+"-提交答案:"+scPickIds[i]);

                    scPoints+=question.getPoint();

                    if(scPickIds[i].equals("-1")) {
                        //未完成
                        scBriefStatus.add(IquizConstant.QUE_UNDONE);
                        continue;
                    }

                    //正确
                    if(question.getAnswerIds().equals(scPickIds[i])){
                        scGet+=question.getPoint();
                        scBriefStatus.add(IquizConstant.QUE_CORRECT);
                    }else
                        //错误
                        scBriefStatus.add(IquizConstant.QUE_WRONG);
                }
                obBriefStatus.addAll(scBriefStatus);
            }
            //多选题
            if(!StringUtils.isNullOrEmpty(origin.getMcQuestionIds())){
                String[] mcQids = origin.getMcQuestionIds().split(",");
                //多选题提交答案格式 题目间,,,相隔；选项间,相隔
                String[] mcAnswer = quizResult.getMcAnswerIds().split(",,,");
                Question question;

                if(mcQids.length!=mcAnswer.length)
                    return -1;

                //多选题对错情况简要
                mcBriefStatus = new ArrayList<>();

                System.out.println("----------多选题判断----------");

                for(int i=0;i<mcQids.length;i++){
                    //获取多选题正确答案
                    question = questionMapper.getQuestionById(mcQids[i]);
                    if(question==null)
                        return -2;
                    mcPoints+=question.getPoint();
                    String[] answerIds = question.getAnswerIds().split(",");
                    String[] mcPickIds = mcAnswer[i].split(",");

                    System.out.println(mcQids[i]+"-题目权值:"+question.getPoint());
                    System.out.println(mcQids[i]+"-正确答案:"+ArrayUtil.toString(answerIds));
                    System.out.println(mcQids[i]+"-提交答案:"+ArrayUtil.toString(mcPickIds));

                    if(mcPickIds[0].equals("-1")) {
                        mcBriefStatus.add(IquizConstant.QUE_UNDONE);
                        continue;
                    }

                    //多选题评分标准，少选一半分，错误没分，全选满分
                    float flag=1;
                    for (String pickId:mcPickIds){

                        if(!ArrayUtil.contains(answerIds,pickId)) {
                            flag = 0;
                            mcBriefStatus.add(IquizConstant.QUE_WRONG);
                            break;
                        }
                    }
                    //少选
                    if(flag!=0&&answerIds.length!=mcPickIds.length) {
                        mcBriefStatus.add(IquizConstant.QUE_HALF_CORRECT);
                        flag = 0.5f;
                    }else if(flag ==1){
                        mcBriefStatus.add(IquizConstant.QUE_CORRECT);
                    }

                    mcGet+=question.getPoint()*flag;
                }
                obBriefStatus.addAll(mcBriefStatus);
            }


            //填空题
            if(!StringUtils.isNullOrEmpty(origin.getBfQuestionIds())){
                String[] bfQids = origin.getBfQuestionIds().split(",");
                String[] bfAnswer = quizResult.getBfAnswer().split("\\*\\^\\*");
                if(bfAnswer.length!=bfQids.length)
                    return -1;

                Question question;
                //填空题对错情况简要
                bfBriefStatus = new ArrayList<>();

                System.out.println("----------填空题判断----------");

                for(int i=0;i<bfQids.length;i++){
                    //获取单选题正确答案
                    question = questionMapper.getQuestionById(bfQids[i]);
                    if(question==null)
                        return -2;

                    bfPoints+=question.getPoint();

                    List<Option> options = optionMapper.getOptionsByIds(question.getAnswerIds().split(","));

                    System.out.println(bfQids[i]+"-题目权值:"+question.getPoint());
                    System.out.println(bfQids[i]+"-正确答案:"+options);
                    System.out.println(bfQids[i]+"-提交答案:"+bfAnswer[i]);

                    if(bfAnswer[i].trim().equals("")){
                        bfBriefStatus.add(IquizConstant.QUE_UNDONE);
                        continue;
                    }

                    float flag =0;
                    for(Option op: options){
                        //填空题评分标准：正确答案集内有存在所提交的答案即为正确
                        if(op.getContent().equals(bfAnswer[i])){
                            bfBriefStatus.add(IquizConstant.QUE_CORRECT);
                            flag=1;
                            break;
                        }
                    }
                    if(flag==0)
                        bfBriefStatus.add(IquizConstant.QUE_WRONG);
                    bfGet+=question.getPoint()*flag;
                }
                obBriefStatus.addAll(bfBriefStatus);
            }


            sumPoints=scPoints+mcPoints+bfPoints;
            //主观题
            if(!StringUtils.isNullOrEmpty(origin.getSubQuestionIds())){
                //主观题在初始提交阶段不做判断，只顺势统计题目权值，并将考试状态设置为“待批阅”
                state=1;

                System.out.println("----------主观题判断----------");

                String[] subQids = origin.getSubQuestionIds().split(",");
                for (String qid:subQids){
                    Question question = questionMapper.getQuestionById(qid);
                    if(question==null)
                        return -2;
                    sumPoints+=question.getPoint();

                    System.out.println(qid+"-题目权值:"+question.getPoint());

                }
            }

            //todo 逻辑待优化，或可将题目总权值存入数据库中


            BigDecimal scScore = NumberUtil.round(scGet * (100 / sumPoints), 2);
            BigDecimal mcScore = NumberUtil.round(mcGet * (100 / sumPoints), 2);
            BigDecimal bfScore = NumberUtil.round(bfGet * (100 / sumPoints), 2);


            System.out.println("---------最后得分----------");
            System.out.println("客观题总分："+NumberUtil.round(scScore.add(mcScore).add(bfScore), 2));
            System.out.println("单选题："+scScore);
            System.out.println("多选题:"+mcScore);
            System.out.println("填空题："+bfScore);
            System.out.println("客观题对错情况简要："+obBriefStatus);


            return quizResultMapper.quizHandIn(quizResult.getId(), quizResult.getScAnswerIds(),
                    quizResult.getMcAnswerIds(),quizResult.getBfAnswer(), quizResult.getSubAnswer(),
                    scScore,mcScore,bfScore, state,String.join(",",obBriefStatus),userId);

        }
        return -1;

    }




    /**
     * 查看考试成绩详情
     * @param id 考试结果id
     **/
    @Override
    public QuizResultVo getQuizResult(String id) {

        //todo 权限判断 本班教师可查看，试卷对应的学生本人可以查看
        QuizResult quizResult = quizResultMapper.getQuizResultById(id);
        if(quizResult!=null&&quizResult.getState()!=0){
            QuizResultVo qrVo = new QuizResultVo();
            qrVo.setId(id);
            //todo 获取用户信息vo
            qrVo.setUid(quizResult.getUid());
            qrVo.setScScore(quizResult.getScScore());
            qrVo.setMcScore(quizResult.getMcScore());
            qrVo.setBfScore(quizResult.getBfScore());
            qrVo.setSubScore(quizResult.getSubScore());
            qrVo.setState(quizResult.getState());
            qrVo.setPerPointScore(quizResult.getPerPointScore());
            if(quizResult.getObBriefStatus()!=null)
                qrVo.setObBriefStatus(quizResult.getObBriefStatus().split(","));


            String[] strs = quizResult.getSubPerScore().split(",");
            List<Double> subPerScore=new ArrayList<>();
            try {
                for (String str:strs){
                    subPerScore.add(Double.parseDouble(str));
                }
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
            qrVo.setSubPerScore(subPerScore);


            //获取考试信息
            Quiz quiz = quizMapper.getQuizById(quizResult.getQid());
            qrVo.setQuiz(quiz);

            //获取题集信息包括答案
            float scPoints=0,mcPoints=0,bfPoints=0,subPoints=0,sumPoints=0;
            Map<String,List<QuestionVo>> map=new HashMap<>();
            //单选题
            if(!StringUtils.isNullOrEmpty(quizResult.getScQuestionIds())){
                String[] scQids = quizResult.getScQuestionIds().split(",");
                Map<String, Object> scMap = IquizTool.GetQuestionsAndPointsByIds(scQids, questionMapper, optionMapper);

                map.put("scQuestions",(List<QuestionVo>) scMap.get("questions"));
                scPoints=(float)scMap.get("point");

                List<String> scAnswerIds = Arrays.asList(quizResult.getScAnswerIds().split(","));
                qrVo.setScAnswerIds(scAnswerIds);

            }

            //多选题
            if(!StringUtils.isNullOrEmpty(quizResult.getMcQuestionIds())){
                String[] mcQids = quizResult.getMcQuestionIds().split(",");
                Map<String, Object> mcMap = IquizTool.GetQuestionsAndPointsByIds(mcQids, questionMapper, optionMapper);

                map.put("mcQuestions",(List<QuestionVo>) mcMap.get("questions"));
                mcPoints=(float)mcMap.get("point");

                List<List<String>> mcAnswerIds = new ArrayList<>();
                for (String str:quizResult.getMcAnswerIds().split(",,,")){
                    mcAnswerIds.add(Arrays.asList(str.split(",")));
                }
                qrVo.setMcAnswerIds(mcAnswerIds);

            }
            //填空题
            if(!StringUtils.isNullOrEmpty(quizResult.getBfQuestionIds())){
                String[] bfQids = quizResult.getBfQuestionIds().split(",");
                Map<String, Object> bfMap = IquizTool.GetQuestionsAndPointsByIds(bfQids, questionMapper, optionMapper);

                map.put("bfQuestions",(List<QuestionVo>) bfMap.get("questions"));
                bfPoints=(float)bfMap.get("point");

                List<String> bfAnswer = Arrays.asList(quizResult.getBfAnswer().split("\\*\\^\\*"));
                qrVo.setBfAnswer(bfAnswer);
            }

            //主观题
            if(!StringUtils.isNullOrEmpty(quizResult.getSubQuestionIds())){
                String[] subQids = quizResult.getSubQuestionIds().split(",");
                Map<String, Object> subMap = IquizTool.GetQuestionsAndPointsByIds(subQids, questionMapper, optionMapper);

                map.put("subQuestions",(List<QuestionVo>) subMap.get("questions"));
                subPoints=(float)subMap.get("point");

                List<String> subAnswer = Arrays.asList(quizResult.getSubAnswer().split("\\*\\^\\*"));
                qrVo.setSubAnswer(subAnswer);
            }
            qrVo.setQuestions(map);

            //统计各题型得分占比
            sumPoints=scPoints+mcPoints+bfPoints+subPoints;

            if(scPoints!=0)
                qrVo.setScAllScore(NumberUtil.round(100*(scPoints/sumPoints),2));
            if(mcPoints!=0)
                qrVo.setMcAllScore(NumberUtil.round(100*(mcPoints/sumPoints),2));
            if(bfPoints!=0)
                qrVo.setBfAllScore(NumberUtil.round(100*(bfPoints/sumPoints),2));
            if(scPoints!=0)
                qrVo.setSubAllScore(NumberUtil.round(100*(subPoints/sumPoints),2));


            List<Integer> rank = rank(quizResultMapper.getRankIdsByQid(quiz.getId()), id);
            if(rank!=null){
                qrVo.setNum(rank.get(0));
                qrVo.setRank(rank.get(1));
            }

            return qrVo;

        }
        return null;
    }


    /**
     * 教师-查看班级学生单次成绩结果集
     * @param qid 考试id
     * @return map集合 'quiz':考试信息；'cqresult':考试结果集
     **/
    @Override
    public Map<String,Object> getClassQuizResults(String qid) {
        Map<String,Object>map=new HashMap<>();

        Quiz quiz = quizMapper.getQuizById(qid);
        if (quiz != null) {
            map.put("quiz",quiz);

            //todo 判断登录用户是否为该班级教师
            //获取所属班级的学生列表
            List<ClassRoomUser> students = classRoomUserMapper.getStudentIdsByCid(quiz.getCid());

            List<QuizResultVo> results = new ArrayList<>();

            if (students.size() > 0) {
                List<String> rankIds = quizResultMapper.getRankIdsByQid(quiz.getId());

                //放置 <"知识点名称",[正确数量,出现总数量]>
                Map<String,Integer[]> knowledgePoints = new HashMap<>();

                for (ClassRoomUser student : students) {
                    QuizResult result = quizResultMapper.getQuizResultByQidAndUid(qid, student.getUid());
                    List<Integer> rank;
                    if (result != null) {
                        QuizResultVo qrVo = new QuizResultVo();
                        qrVo.setStudent(student);
                        qrVo.setId(result.getId());
                        qrVo.setStudent(student);
                        qrVo.setScScore(result.getScScore());
                        qrVo.setMcScore(result.getMcScore());
                        qrVo.setBfScore(result.getBfScore());
                        qrVo.setSubScore(result.getSubScore());
                        qrVo.setState(result.getState());
                        qrVo.setPerPointScore(result.getPerPointScore());

                        qrVo.setRank(rank(rankIds, result.getId())
                                            .get(1));

                        //知识点正确率统计
                        qrVo.setObBriefStatus(result.getObBriefStatus().split(","));

                        String []qids = new String[0];
                        if(result.getScQuestionIds()!=null){
                            String[] scQid = result.getScQuestionIds().split(",");
                            qids=ArrayUtil.addAll(scQid);
                        }
                        if(result.getMcQuestionIds()!=null){
                            String[] mcQid = result.getMcQuestionIds().split(",");
                            qids=ArrayUtil.addAll(qids,mcQid);
                        }
                        if(result.getBfQuestionIds()!=null){
                            String[] bfQid = result.getBfQuestionIds().split(",");
                            qids=ArrayUtil.addAll(qids,bfQid);
                        }

                        String[] obBriefStatus = result.getObBriefStatus().split(",");

                        if(qids.length>0){
                            for (int i=0;i<qids.length;i++){
                                Question question = questionMapper.getQuestionById(qids[i]);
                                if(question!=null){
                                    //获取知识点名称
                                    if(StringUtils.isNullOrEmpty(question.getKnowledge()))
                                        continue;
                                    Integer[] correctAndSum = knowledgePoints.get(question.getKnowledge());
                                    //若map里没有此知识点便新增该键值对
                                    if(correctAndSum==null){
                                        knowledgePoints.put(question.getKnowledge(),
                                                new Integer[]{obBriefStatus[i].equals(IquizConstant.QUE_CORRECT)?1:0,1});
                                    }else{
                                        knowledgePoints.put(question.getKnowledge(),
                                                new Integer[]{obBriefStatus[i].equals(IquizConstant.QUE_CORRECT)
                                                        ?correctAndSum[0]+1:correctAndSum[0],correctAndSum[1]+1});
                                    }
                                }
                            }
                        }
                        results.add(qrVo);
                    }
                }
                map.put("knowledge",knowledgePoints);
                map.put("cqresult",results);
                return map;
            }

        }
        return null;
    }

    /**
     * 教师-对主观题进行批阅
     * @param subPerScore 主观题得分 以,分割
     * @param id 考试结果id
     **/
    @Override
    public int mark(String subPerScore, String id) {
        QuizResult result = quizResultMapper.getQuizResultById(id);
        if(result!=null){
            String[] scores = subPerScore.split(",");
            double sum=0,sc;
            try {
                for (int i=0;i<scores.length;i++){
                    sum+=(Integer.parseInt(scores[i]));
                }
            }catch (Exception e){
                e.printStackTrace();
                return 0;
            }
            //todo 判断登录用户是否有权限进行评阅
            return quizResultMapper.mark(subPerScore,sum,id);
        }
        return -1;


    }




    /**
     * 学生-获取待完成的考试信息
     **/
    @Override
    public List<QuizPaperVo> getBeToCompletedQuizzes() {

        String userId = IquizTool.getUserId();
        List<QuizResult> results = quizResultMapper.getQuizResultsByUid(userId);
        QuizPaperVo qrVo = null;
        List<QuizPaperVo>list=new ArrayList<>();

        for (QuizResult qr:results){
            Quiz quiz = quizMapper.getQuizById(qr.getQid());
            if(quiz!=null){
                qrVo=new QuizPaperVo(qr.getId(),qr.getUid(),quiz);
                list.add(qrVo);
            }else
                return null;
        }
        return list;
    }


    /**
     * 排名计算
     * @param ids 按成绩倒序查找出的学生id集合
     * @param id 所计算排名的学生考试结果信息id
     * @return list第一个元素放置考试总人数，第二个元素放置排名
     **/
    public List<Integer> rank(List<String> ids,String id){
        //按照总分对试卷id进行排序
        if(ids.size()>0){
            List<Integer> list=new ArrayList<>();
            for(int i=0;i<ids.size();i++){
                if(ids.get(i).equals(id)){
                    list.add(ids.size());
                    list.add(i+1);
                    return list;
                }
            }
        }
        return null;
    }
}
