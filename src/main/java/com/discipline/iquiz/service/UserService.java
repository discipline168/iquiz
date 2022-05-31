package com.discipline.iquiz.service;

import java.util.Map;

public interface UserService {
    Map<String,String> jwtLogin(String username, String password) throws Exception;
}
