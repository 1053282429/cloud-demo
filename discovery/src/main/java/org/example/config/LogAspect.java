package org.example.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author 10532
 */
@Aspect
@Component
public class LogAspect {

    Logger logger = LoggerFactory.getLogger(LogAspect.class);


    @Pointcut("execution(public * org.example.controller..*.*(..))")
    public void pointMethod(){}


    /**
     * 在方法调用之前，打印入参
     */
    @Before("pointMethod()")
    public void before(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder params = new StringBuilder();
        for (Object arg : args) {
            params.append(arg).append(" ");
        }
        logger.info("param:" + params.toString());
    }


    /**
     * 返回之后，打印出参
     */
    @AfterReturning(pointcut="pointMethod()", returning = "returnVal")
    public void afterReturn(JoinPoint joinPoint, Object returnVal) {
        logger.info("return:" + returnVal.toString());
    }

}