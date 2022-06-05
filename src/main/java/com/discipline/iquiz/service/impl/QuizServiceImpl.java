package com.discipline.iquiz.service.impl;

import com.discipline.iquiz.mapper.ClassRoomUserMapper;
import com.discipline.iquiz.mapper.QuestionMapper;
import com.discipline.iquiz.mapper.QuizMapper;
import com.discipline.iquiz.mapper.QuizResultMapper;
import com.discipline.iquiz.po.ClassRoomUser;
import com.discipline.iquiz.po.Question;
import com.discipline.iquiz.po.Quiz;
import com.discipline.iquiz.po.QuizResult;
import com.discipline.iquiz.service.QuizService;
import com.discipline.iquiz.util.IquizConstant;
import com.discipline.iquiz.util.IquizTool;
import com.mysql.jdbc.StringUtils;
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
    @Resource
    QuizResultMapper quizResultMapper;
    @Resource
    QuestionMapper questionMapper;


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
            result=quizMapper.addQuiz(id,quiz.getName(),userId,quiz.getCid(),quiz.getMode(),
                    quiz.getRandomNum(),quiz.getQbankId(),quiz.getIsPreview(),quiz.getIsRandomOption(),
                    quiz.getDuration(),quiz.getTime(),quiz.getQuestionIds());
        }catch (Exception e){
            e.printStackTrace();
            id = IquizTool.generateRandomString("quiz_", 8, false);
            result=quizMapper.addQuiz(id,quiz.getName(),userId,quiz.getCid(),quiz.getMode(),
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

    /**
     * 教师-启用考试发放试卷
     **/
    @Override
    public int open(String id) {
        //获取考试信息
        Quiz quiz = quizMapper.getQuizById(id);
        if(quiz!=null){
            //获取所属班级的学生列表
            //todo 登录教师权限判断
            List<ClassRoomUser> students = classRoomUserMapper.getStudentIdsByCid(quiz.getCid());


            int points=0;

            List<String> scQids=new ArrayList<>();
            List<String> mcQids=new ArrayList<>();
            List<String> bfQids=new ArrayList<>();
            List<String> subQids=new ArrayList<>();


            //固定出题模式
            if(quiz.getMode()==IquizConstant.NORMAL_MODE_QUIZ){
                String[] qids = quiz.getQuestionIds().split(",");
                if(qids.length>0){
                    Question que;
                    for(String qid:qids){
                        que = questionMapper.getQuestionById(qid);
                        if(que!=null){
                            points+=que.getPoint();
                            if(que.getType()==IquizConstant.SINGLE_CHOICE_QUESTION)
                                scQids.add(qid);
                            else if(que.getType()==IquizConstant.MULTI_CHOICE_QUESTION)
                                mcQids.add(qid);
                            else if(que.getType()==IquizConstant.BLANK_FILLING_QUESTION)
                                bfQids.add(qid);
                            else if (que.getType()==IquizConstant.SUBJECTIVE_QUESTION)
                                subQids.add(qid);
                        }
                    }
                }else
                    return -1;
            }

            //随机出题模式
            if(quiz.getMode()==IquizConstant.RANDOM_MODE_QUIZ){
                String[] randomNum = quiz.getRandomNum().split(",");
                String qbankId = quiz.getQbankId();
                if(randomNum.length>0&& !StringUtils.isNullOrEmpty(qbankId)){

                    for (int i=0;i<randomNum.length;i++){
                        try {
                            int num=Integer.parseInt(randomNum[i]);

                            if(i+1==IquizConstant.SINGLE_CHOICE_QUESTION&&num>0){
                                List<Question> scQues = questionMapper.getRandomQuestionByTypeAndNum(i + 1, num);
                                for(Question que:scQues){
                                    points+=que.getPoint();
                                    scQids.add(que.getId());
                                }


                            }else if(i+1==IquizConstant.MULTI_CHOICE_QUESTION&&num>0){
                                List<Question> mcQues = questionMapper.getRandomQuestionByTypeAndNum(i + 1, num);
                                for(Question que:mcQues){
                                    points+=que.getPoint();
                                    mcQids.add(que.getId());
                                }
                            }
                            else if(i+1==IquizConstant.BLANK_FILLING_QUESTION&&num>0){
                                List<Question> bfQues = questionMapper.getRandomQuestionByTypeAndNum(i + 1, num);
                                for(Question que:bfQues){
                                    points+=que.getPoint();
                                    bfQids.add(que.getId());
                                }
                            }else if(i+1==IquizConstant.SUBJECTIVE_QUESTION&&num>0){
                                List<Question> subQues = questionMapper.getRandomQuestionByTypeAndNum(i + 1, num);
                                for(Question que:subQues){
                                    points+=que.getPoint();
                                    subQids.add(que.getId());
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            return -1;
                        }
                    }



                }
                else
                    return -1;
            }


            //循环班级学生列表发放试卷
            for (ClassRoomUser stu:students){
                int result = quizResultMapper.addQuizResult(id + "_" + stu.getUid(), id, stu.getUid(),
                        scQids.size() > 0 ? String.join(",", scQids) : null,
                        mcQids.size() > 0 ? String.join(",", mcQids) : null,
                        bfQids.size() > 0 ? String.join(",", bfQids) : null,
                        subQids.size() > 0 ? String.join(",", subQids) : null, 100.0 / points);
                if(result<0)
                    return -1;
            }

            //将考试状态设为启用
            return quizMapper.updateQuizStatus(id, IquizConstant.QUIZ_ACTIVATED);

        }

        return -1;
    }

}
