package org.example.controller;

import org.example.po.Result;
import org.example.service.DiscoveryNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: 边俊超
 * @Date: 2023/10/27 16:38
 */
@RestController
@RequestMapping("/node")
public class DiscoveryNodeController {

    Logger logger = LoggerFactory.getLogger(DiscoveryNodeController.class);

    @Autowired
    private DiscoveryNodeService discoveryNodeService;

    @PostMapping("/exchange/info")
    public Result<Map<String, Integer>> exchangeInfo(@RequestBody Map<String, Integer> exchangeInfo){
        return Result.success(discoveryNodeService.exchangeInfo(exchangeInfo));
    }


}
