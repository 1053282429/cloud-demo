package org.example.config;


import org.example.po.Result;
import org.example.po.ServiceException;
import org.example.po.Status;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;


/**
 * @author 10532
 */
@ControllerAdvice
@ResponseBody
public class ApiExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<Object> exceptionHandler(Exception e, HandlerMethod hm) {

        if (e.getClass() == ServiceException.class) {
            ServiceException serviceException = (ServiceException)e;
            return new Result<>(serviceException.getCode(), serviceException.getEnMsg());
        }

        return new Result<>(Status.INTERNAL_SERVER_ERROR_ARGS.getCode(), Status.INTERNAL_SERVER_ERROR_ARGS.getEnMsg());
    }

}

