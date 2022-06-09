package com.discipline.iquiz.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerUtil;
import cn.hutool.extra.tokenizer.Word;
import cn.hutool.extra.tokenizer.engine.hanlp.HanLPEngine;
import cn.hutool.extra.tokenizer.engine.jieba.JiebaEngine;
import com.discipline.iquiz.jwt.util.JWTUtil;
import com.discipline.iquiz.mapper.OptionMapper;
import com.discipline.iquiz.mapper.QuestionMapper;
import com.discipline.iquiz.po.Option;
import com.discipline.iquiz.po.Question;
import com.discipline.iquiz.vo.QuestionVo;
import com.hankcs.hanlp.HanLP;
import com.mysql.jdbc.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jsoup.Jsoup;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
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
     * @param optionMapper
     * @param isShowAnswer 是否展示答案
     **/
    public static QuestionVo formatQue(Question que, OptionMapper optionMapper, boolean isShowAnswer){
        QuestionVo questionVo=new QuestionVo(que.getId(),que.getContent(),que.getType(),que.getPoint(),que.getKnowledge());

        //若为选择题
        if(que.getType()==IquizConstant.SINGLE_CHOICE_QUESTION||
                que.getType()==IquizConstant.MULTI_CHOICE_QUESTION){
            //获取选项集合
            List<Option> options = optionMapper.getOptionsByIds(que.getOptionIds().split(","));
            if(options.size()>0)
                questionVo.setOptions(options);
        }
        //获取正确选项（答案）集合
        if(isShowAnswer&& !StringUtils.isNullOrEmpty(que.getAnswerIds())){
            List<Option> answer = optionMapper.getOptionsByIds(que.getAnswerIds().split(","));
            if(answer.size()>0)
                questionVo.setAnswer(answer);
        }
        return questionVo;
    }



    /**
     * 根据id数组查询题目信息并装饰成vo集合
     * @param ids 题目id数组
     * @param questionMapper
     * @param optionMapper
     * @return 题集信息和该题集的总权值
     **/
    public static Map<String,Object> GetQuestionsAndPointsByIds(String[] ids, QuestionMapper questionMapper,OptionMapper optionMapper){
        ArrayList<QuestionVo> questions = new ArrayList<>();
        float point=0;
        Map<String, Object> map = new HashMap<>();

        for(String qid:ids){
            Question que = questionMapper.getQuestionById(qid);
            if(que==null)
                return null;
            QuestionVo questionVo = IquizTool.formatQue(que, optionMapper, true);
            point+=questionVo.getPoint();
            questions.add(questionVo);
        }
        map.put("questions",questions);
        map.put("point",point);
        return map;
    }




    /**
     * 分词并计算相似度
     * 工具类
     */
    //阈值
    public static double THRESHOLD = 0.2;

    /**
     * 通过Ik 进行将句子分词
     * @param text
     */
    public static Vector<String> participleIk(String text) {
        //对输入进行分词
        Vector<String> str = new Vector<>();
        try {
            StringReader reader = new StringReader(text);
            //当为true时，分词器进行最大词长切分
            IKSegmenter ik = new IKSegmenter(reader, true);
            Lexeme lexeme = null;
            while ((lexeme = ik.next()) != null) {
                str.add(lexeme.getLexemeText());
            }
            if (str.size() == 0) {
                return null;
            }
            //分词后
        } catch (IOException e) {
        }
        return str;
    }


    /**
     * 结巴分词
     * @param text
     * @return
     */
    public static Vector<String> participleJieBa(String text) {
        JiebaEngine engine = new JiebaEngine();
        Result results = engine.parse(text);
        //输出：这 两个 方法 的 区别 在于 返回 值
        String result = CollUtil.join((Iterator<Word>) results, ",");
        return new Vector<>(Arrays.asList(result.split(",")));
    }


    /**
     * 中文分词
     * @param text
     * @return
     */
    public static Vector<String> participleChinese(String text){
        //自动根据用户引入的分词库的jar来自动选择使用的引擎
        TokenizerEngine engine = TokenizerUtil.createEngine();
        //解析文本
        //String text = "这两个方法的区别在于返回值";
        Result results = engine.parse(text);
        //输出：这 两个 方法 的 区别 在于 返回 值
        String result = CollUtil.join((Iterator<Word>)results, ",");
        return new Vector<>(Arrays.asList(result.split(",")));
    }

    /**
     * 采用 HanLP 进行自定义分词
     */
    public static Vector<String> participleHanLP(String text){
        TokenizerEngine engine = new HanLPEngine();
        //解析文本
        //String text = "这两个方法的区别在于返回值";
        Result results = engine.parse(text);
        //输出：这 两个 方法 的 区别 在于 返回 值
        String result = CollUtil.join((Iterator<Word>)results, ",");
        return  new Vector<>(Arrays.asList(result.split(",")));
    }


    /**
     * 返回百分比计算
     * @param TOne
     * @param TTwo
     * @return
     */
    public static double getSimilarity(Vector<String> TOne, Vector<String> TTwo) throws Exception {
        int sizeOne = 0, sizeTwo = 0;
        if (TOne != null && (sizeOne = TOne.size()) > 0 && TTwo != null && (sizeTwo = TTwo.size()) > 0) {
            Map<String, double[]> T = new HashMap<>();
            //T1和T2的并集T
            String index = null;
            for (int i = 0; i < sizeOne; i++) {
                index = TOne.get(i);
                if (index != null) {
                    double[] c = T.get(index);
                    c = new double[2];
                    //T1的语义分数Ci
                    c[0] = 1;
                    //T2的语义分数Ci
                    c[1] = THRESHOLD;
                    T.put(index, c);
                }
            }
            for (int i = 0; i < sizeTwo; i++) {
                index = TTwo.get(i);
                if (index != null) {
                    double[] c = T.get(index);
                    if (c != null && c.length == 2) {
                        //T2中也存在，T2的语义分数=1
                        c[1] = 1;
                    } else {
                        c = new double[2];
                        //T1的语义分数Ci
                        c[0] = THRESHOLD;
                        //T2的语义分数Ci
                        c[1] = 1;
                        T.put(index, c);
                    }
                }
            }
            //开始计算，百分比
            Iterator<String> it = T.keySet().iterator();
            //S1、S2
            double sOne = 0, sTwo = 0, Ssum = 0;
            while (it.hasNext()) {
                double[] c = T.get(it.next());
                Ssum += c[0] * c[1];
                sOne += c[0] * c[0];
                sTwo += c[1] * c[1];
            }
            //百分比
            return Ssum / Math.sqrt(sOne * sTwo);
        } else {
            throw new Exception("传入参数有问题！");
        }
    }


    /**
     * Java利用hanlp完成语句相似度分析
     * @param sentenceOne
     * @param sentenceTwo
     * @return
     */
    public static double findSimilarity(String sentenceOne, String sentenceTwo) {
        List<String> sentOneWords = getSplitWords(sentenceOne);
        List<String> sentTwoWords = getSplitWords(sentenceTwo);
        List<String> allWords = mergeList(sentOneWords, sentTwoWords);
        int[] statisticOne = statistic(allWords, sentOneWords);
        int[] statisticTwo = statistic(allWords, sentTwoWords);
        double dividend = 0;
        double divisor1 = 0;
        double divisor2 = 0;
        int length = statisticOne.length;
        for (int i = 0; i < length; i++) {
            dividend += statisticOne[i] * statisticTwo[i];
            divisor1 += Math.pow(statisticOne[i], 2);
            divisor2 += Math.pow(statisticTwo[i], 2);
        }
        return dividend / (Math.sqrt(divisor1) * Math.sqrt(divisor2));
    }


    private static int[] statistic(List<String> allWords, List<String> sentWords) {
        int[] result = new int[allWords.size()];
        int size = allWords.size();
        for (int i = 0; i < size; i++) {
            result[i] = Collections.frequency(sentWords, allWords.get(i));
        }
        return result;
    }


    /**
     * 去重
     * @param listOne
     * @param listTwo
     * @return
     */
    private static List<String> mergeList(List<String> listOne, List<String> listTwo) {
        List<String> result = new ArrayList<>();
        result.addAll(listOne);
        result.addAll(listTwo);
        return result.stream().distinct().collect(Collectors.toList());
    }


    /**
     * 过滤标签
     * @param sentence
     * @return
     */
    private static List<String> getSplitWords(String sentence) {
        // 去除掉html标签
        sentence = Jsoup.parse(sentence.replace(" ","")).body().text();
        // 标点符号会被单独分为一个Term，去除之
        return HanLP.segment(sentence).stream().map(a -> a.word).filter(s -> !"`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？ ".contains(s)).collect(Collectors.toList());
    }

}
