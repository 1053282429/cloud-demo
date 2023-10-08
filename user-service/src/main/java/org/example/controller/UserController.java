package org.example.controller;

import org.example.util.HttpClientUtil;
import org.example.util.HttpContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: 边俊超
 * @Date: 2023/10/7 17:17
 */
@RestController
@RequestMapping("/user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/get")
    public String getUser() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String traceId = request.getHeader("trace-id");
        String url = "http://192.168.1.4:8081/discovery/discovery/get";
        try {
            String s = HttpClientUtil.doGet(url);
            logger.info(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info(traceId);
        return "111112";
    }

}
