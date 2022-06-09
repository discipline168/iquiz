package com.discipline.iquiz.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserVo {

    private String id;
    private String username;
    private int role;
    private String avatar;
}
