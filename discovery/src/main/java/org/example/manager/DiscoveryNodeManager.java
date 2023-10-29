package org.example.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author: 边俊超
 * @Date: 2023/10/27 15:13
 *
 * discovery 集群部署节点管理
 */
public class DiscoveryNodeManager {

    /**
     * 用于和别的节点交换discovery节点信息  健康的节点写入健康列表
     * 发现非健康的则从健康列表中剔除  健康列表剔除后等待和所有实例都通信两轮过后再从交换列表剔除
     */
    private final static ConcurrentHashMap<String, Integer> discoveryNodes = new ConcurrentHashMap<>();

    /**
     * 健康列表
     */
    private final static CopyOnWriteArraySet<String> discoveryHealthNodes = new CopyOnWriteArraySet<>();

    /**
     * 获取健康的实例列表
     */
    public static List<String> getHealthNodeList() {
        return new ArrayList<>(discoveryHealthNodes);
    }

    /**
     * 获取用来交换的实例信息
     */
    public static Map<String, Integer> getExchangeInfo() {
        return new HashMap<>(discoveryNodes);
    }

    /**
     * 接受交换信息
     */
    public static void acceptExchangeInfo(Map<String, Integer> info) {
        if (info.isEmpty()) {
            return;
        }
        List<String> exceptionList = new ArrayList<>();
        info.forEach((k,v) -> {
            // 健康实例加入列表
            if (1 == v) {
                discoveryNodes.putIfAbsent(k, 1);
                discoveryHealthNodes.add(k);
            } else {
                // 如果从别的节点获取到某个节点有异常，自己这边还是正常的，则立即触发检查
                if (discoveryNodes.get(k) == 1) {
                    exceptionList.add(k);
                }
            }
        });
        // 检查异常
        exceptionList.forEach(k -> {});
    }











}
