package com.discipline.iquiz.po;

import lombok.Data;

@Data
public class User {
    private String id;
    private int role;
    private String username;
    private String password;
    private String salt;
    private String avatar;
}
