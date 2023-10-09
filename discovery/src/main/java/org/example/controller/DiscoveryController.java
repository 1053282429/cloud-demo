package org.example.controller;

import org.example.po.Keepalive;
import org.example.po.Register;
import org.example.po.Result;
import org.example.po.Subscribe;
import org.example.service.DiscoveryService;
import org.example.util.HttpContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 10532
 */
@RestController
@RequestMapping("/discovery")
public class DiscoveryController {

    Logger logger = LoggerFactory.getLogger(DiscoveryController.class);

    @Autowired
    private DiscoveryService discoveryService;

    @GetMapping("/get")
    public String getList() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String traceId = request.getHeader("trace-id");
        logger.info(traceId);
        return "222222";
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody Register register){
        return Result.success(discoveryService.register(register));
    }

    @PostMapping("/keepalive")
    public Result<Object> keepalive(@RequestBody Keepalive keepalive){
        discoveryService.keepalive(keepalive);
        return Result.success();
    }

    @PostMapping("/cancel")
    public Result<Object> cancel(@RequestBody Keepalive keepalive){
        discoveryService.cancel(keepalive);
        return Result.success();
    }

    @PostMapping("/subscribe")
    public Result<Object> subscribe(@RequestBody Subscribe subscribe){
        return Result.success(discoveryService.subscribe(subscribe));
    }

}
