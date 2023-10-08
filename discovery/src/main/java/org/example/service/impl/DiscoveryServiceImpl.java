package org.example.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.example.config.RedisService;
import org.example.constant.Constant;
import org.example.po.Instance;
import org.example.po.Register;
import org.example.po.Server;
import org.example.service.DiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: 边俊超
 * @Date: 2023/10/8 15:37
 */
@Service
public class DiscoveryServiceImpl implements DiscoveryService {

    @Autowired
    private RedisService redisService;

    @Override
    public void register(Register register) {

        String name = register.getName();
        Object result = redisService.getCacheMapValue(Constant.REDIS_DISCOVERY_SERVER_LIST_KEY, name);
        Server newServer = new Server();

        // 如果redis中不存在该服务，则新建
        //存在服务则更新实例列表
        if (result == null) {
            newServer.setName(register.getName());
            newServer.setGroup(register.getGroup());
            newServer.setAvailable(1);
        } else {
            newServer = JSONObject.parseObject(result.toString(), Server.class);
        }
        List<Instance> instances = newServer.getInstances() == null ?  new ArrayList<>() : newServer.getInstances();

        AtomicInteger count = new AtomicInteger(0);
        instances.forEach(k -> {
            if (k.getIp().equals(register.getIp())
                    && k.getPort().equals(register.getPort())
                    && k.getMacAddress().equals(register.getMacAddress())) {
                count.getAndIncrement();
                k.setStatus(0);
                k.setMode(register.getMode());
                k.setHealthy(1);
            }
        });
        if (count.get() == 0) {
            Instance instance = Instance.builder()
                    .healthy(1)
                    .macAddress(register.getMacAddress())
                    .remove(register.getRemove())
                    .status(0)
                    .mode(register.getMode())
                    .ip(register.getIp())
                    .port(register.getPort())
                    .name(register.getName())
                    .build();
            instances.add(instance);
        }
        newServer.setInstances(instances);
        redisService.setCacheMapValue(Constant.REDIS_DISCOVERY_SERVER_LIST_KEY, name, JSON.toJSONString(newServer));

    }
}
