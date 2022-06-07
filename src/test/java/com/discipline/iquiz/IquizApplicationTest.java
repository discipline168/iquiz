package com.discipline.iquiz;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import com.discipline.iquiz.dto.QuestionDto;
import com.discipline.iquiz.mapper.OptionMapper;
import com.discipline.iquiz.service.impl.QuestionServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class IquizApplicationTest {

    @Resource
    OptionMapper optionMapper;
    @Resource
    QuestionServiceImpl questionService;

    @Test
    void utilTest(){
        System.out.println(RandomUtil.randomString(6));
    }




    @Test
    void addQue(){
        QuestionDto que = new QuestionDto();

        que.setContent("220526测试单选题");
        que.setPoint(2);
        que.setType(1);
        que.setQbankId("qbank_ow18HX");

        que.setOptionContents(new String[]{"220526测试选项-01","220526测试选项-02","220526测试选项-03","220526测试选项-04"});
        que.setAnswerIndexes(new int []{3});

        questionService.addQuestionToQbank(que);
    }

    @Test
    void just(){
        System.out.println(ArrayUtil.addAll(new String[0])[0]);
    }

}
