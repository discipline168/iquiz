package com.discipline.iquiz.service.impl;

import com.discipline.iquiz.mapper.QbankMapper;
import com.discipline.iquiz.po.Qbank;
import com.discipline.iquiz.service.QbankService;
import com.discipline.iquiz.util.IquizConstant;
import com.discipline.iquiz.util.IquizTool;
import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class QbankServiceImpl implements QbankService {
    @Resource
    QbankMapper qbankMapper;


    /**
     * 获取班级所持有的题库信息
     * @param cid 班级id
     **/
    @Override
    public List<Qbank> getClassQbanks(String cid) {
        String userId = IquizTool.getUserId();
        return qbankMapper.getQbanksByCidAndTid(cid, userId);
    }

    /**
     * 新增题库信息
     * @param name 题库名称
     * @param cid 所属课堂id
     **/
    @Override
    public String addQbank(String name, String cid) {
        String userId = IquizTool.getUserId();

        if(!StringUtils.isNullOrEmpty(userId)){
            int result;
            String id=IquizTool.generateRandomString("qbank_",6,false);
            Date time=new Date();
            try {
                result=qbankMapper.addQbank(id,name,userId,cid,time);
            }catch (Exception e){
                e.printStackTrace();
                id=IquizTool.generateRandomString("qbank_",6,false);
                result=qbankMapper.addQbank(id,name,userId,cid,time);
            }
            if (result!=0)
                return id;
        }
        return null;
    }

    /**
     * 逻辑删除题库
     * @param id 题库id
     **/
    @Override
    public int deletQbank(String id) {
        String userId = IquizTool.getUserId();
        return qbankMapper.deletQbankByIdAndTid(id, userId);
    }
}
