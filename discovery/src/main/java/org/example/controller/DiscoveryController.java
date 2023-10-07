package org.example.controller;

import org.example.util.HttpContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 10532
 */
@RestController
@RequestMapping("discovery")
public class DiscoveryController {

    Logger logger = LoggerFactory.getLogger(DiscoveryController.class);


    @GetMapping("get")
    public String getList() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String traceId = request.getHeader("trace-id");
        logger.info(traceId);
        return "222222";
    }

}
