package com.discipline.iquiz.handler;

import com.discipline.iquiz.vo.JsonData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.annotation.Resource;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @Resource
    ObjectMapper objectMapper;

    @ExceptionHandler({UnauthenticatedException.class,UnknownAccountException.class, DisabledAccountException.class})
    public String handleNullAccountException() throws JsonProcessingException {
        return objectMapper.writeValueAsString(JsonData.tokenNull());
    }
    @ExceptionHandler(ExpiredCredentialsException.class)
    public String handleExpiredAccountException() throws JsonProcessingException {
        return objectMapper.writeValueAsString(JsonData.tokenExpired());
    }
    @ExceptionHandler(AuthorizationException.class)
    public String handleRoleWrongException() throws JsonProcessingException {
        return objectMapper.writeValueAsString(JsonData.authorizeWrong());
    }

}
