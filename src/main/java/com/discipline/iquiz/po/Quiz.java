package com.discipline.iquiz.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Quiz {
    private String id;
    private String name;
    private String cid;
    private String tid;
    private int mode;
    private String randomNum;
    private String qbankId;
    private int isRandomOption;
    private int isPreview;
    private int subAuto;
    private int duration;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date time;
    private String questionIds;
    private int status;
}
