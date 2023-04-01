package com.ithima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/*

全局异常处理，使用了AOP的原则横切进去，代替了原来的try catch的麻烦
(annotations = {RestController.class})表示拦截这些加了RestController注解的类
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    //捕获SQl的异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //违反了这个原则
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split =ex.getMessage().split("  JacksonObjectMapper.java");
            String msg=split[2]+"已经存在";
            return R.error(msg);
        }

        return R.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){

        return R.error(ex.getMessage());
    }


}
