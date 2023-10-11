package org.example.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.example.config.RedisService;
import org.example.constant.Constant;
import org.example.po.*;
import org.example.service.DiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @Author: 边俊超
 * @Date: 2023/10/8 15:37
 */
@Service
public class DiscoveryServiceImpl implements DiscoveryService {

    @Autowired
    private RedisService redisService;

    @Override
    public String register(Register register) {

        String name = register.getName();
        Integer port = register.getPort();
        String ip = register.getIp();
        String macAddress = register.getMacAddress();
        String suffix = Constant.UNDERLINE + name + Constant.AMPERSAND + ip + Constant.AMPERSAND + port + Constant.AMPERSAND + macAddress;
        String redisKey = getRedisKey(register.getGroup(), name);
        Map<String, Object> serverList = redisService.getCacheMap(redisKey);
        String token = null;
        serverList = serverList == null ? new HashMap<>() : serverList;

        // 如果redis中存在该实例，则更新实例健康状态
        // 不存在该实例则新建
        for (Map.Entry<String, Object> entry : serverList.entrySet()) {
            if (entry.getKey().endsWith(suffix)) {
                token = entry.getKey();
                Instance instance = JSONObject.parseObject(entry.getValue().toString(), Instance.class);
                instance.setStatus(0);
                instance.setMode(register.getMode());
                instance.setHealthy(1);
                serverList.put(entry.getKey(), JSON.toJSONString(instance));
            }
        }
        if (token == null) {
            token = UUID.randomUUID() + suffix;
            Instance instance = Instance.builder()
                    .healthy(1)
                    .macAddress(register.getMacAddress())
                    .remove(register.getRemove())
                    .status(0)
                    .mode(register.getMode())
                    .ip(ip)
                    .port(port)
                    .name(name)
                    .build();
            serverList.put(token, JSON.toJSONString(instance));
        }
        redisService.setCacheMap(redisKey, serverList);
        return token;
    }

    @Override
    public void keepalive(Keepalive keepalive) {
        String redisKey = getRedisKey(keepalive.getGroup(), keepalive.getName());
        Object cacheMapValue = redisService.getCacheMapValue(redisKey, keepalive.getToken());

        if (ObjectUtils.isEmpty(cacheMapValue)) {
            throw new ServiceException(Status.SERVER_INSTANCE_IS_NOT_EXIST);
        }

        Instance instance = JSONObject.parseObject(cacheMapValue.toString(), Instance.class);
        instance.setStatus(0);
        instance.setHealthy(1);
        redisService.setCacheMapValue(redisKey, keepalive.getToken(), JSON.toJSONString(instance));
    }

    @Override
    public void cancel(Keepalive keepalive) {
        String redisKey = getRedisKey(keepalive.getGroup(), keepalive.getName());
        if (redisService.hasCacheMapKey(redisKey, keepalive.getToken())) {
            throw new ServiceException(Status.SERVER_INSTANCE_IS_NOT_EXIST);
        }
        redisService.deleteCacheMapKey(redisKey, keepalive.getToken());
    }

    @Override
    public Object subscribe(Subscribe subscribe) {

        List<String> keys = new ArrayList<>();
        if (subscribe.getSubAll() == 1) {
            try {
                // TODO: 2023/10/11 考虑本地缓存  group servername 用以直接拼接redis key 不用去扫描redis
                // TODO: 2023/10/11 考虑discovery多实例上线时同步缓存结果
                // TODO: 2023/10/11 考虑本地缓存所有实例列表减少网络io，实例上下线更新缓存   考虑多实例discovery的实例上下线问题
                // TODO: 2023/10/11 考虑完全去掉redis  全部本地缓存实现  discovery多实例之间通过一致性协议同步数据
                // TODO: 2023/10/11 考虑一致性协议实现方案   通信协议选择 http?rpc?tcp?
                keys = redisService.getKeysByPrefix(Constant.DISCOVERY_REDIS_KEY_SERVER_LIST_PREFIX);
            } catch (IOException e) {
                throw new ServiceException(Status.INTERNAL_SERVER_ERROR_ARGS);
            }
        } else {
            for (Subscribe.Server server : subscribe.getServer()) {
                keys.add(getRedisKey(server.getGroup(), server.getName()));
            }
        }

        List<Map<String, String>> maps = redisService.mgetForHash(keys);
        List<Server> servers = new ArrayList<>();
// TODO: 2023/10/11 reids 序列化问题 
        maps.forEach(map -> {
            Server server = new Server();
            servers.add(server);
            List<Instance> instances = new ArrayList<>();
            server.setInstances(instances);
            map.forEach((key, value) -> {
                String replace = key.replace(Constant.DISCOVERY_REDIS_KEY_SERVER_LIST_PREFIX, "");
                String[] split = replace.split(Constant.UNDERLINE);
                server.setGroup(split[0]);
                server.setName(split[1]);
                Instance instance = JSONObject.parseObject(value, Instance.class);
                instances.add(instance);
            });
        });


        return servers;
    }

    /**
     * 获取实例对应的redis key
     * @param group 分组
     * @param name  服务名
     * @return redis key
     */
    private String getRedisKey(String group, String name) {
        return Constant.DISCOVERY_REDIS_KEY_SERVER_LIST_PREFIX + group + Constant.UNDERLINE + name;
    }
}
