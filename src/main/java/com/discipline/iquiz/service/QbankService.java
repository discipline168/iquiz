package com.discipline.iquiz.service;


import com.discipline.iquiz.po.Qbank;

import java.util.List;

public interface QbankService {
    List<Qbank> getClassQbanks(String cid);
    String addQbank(String name,String cid);
    int deletQbank(String cid);
}
