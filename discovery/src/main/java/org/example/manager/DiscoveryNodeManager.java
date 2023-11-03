package org.example.manager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author: 边俊超
 * @Date: 2023/10/27 15:13
 *
 * discovery 集群部署节点管理
 */
public class DiscoveryNodeManager {

    public static final Integer NODE_HEALTHY = 1;
    public static final Integer NODE_EXCEPTION = 0;
    /**
     * 用于和别的节点交换discovery节点信息  健康的节点写入健康列表
     * 发现非健康的则从健康列表中剔除  健康列表剔除后等待和所有实例都通信两轮过后再从交换列表剔除
     */
    private static ConcurrentHashMap<String, Integer> discoveryNodes;

    /**
     * 健康列表
     */
    private static CopyOnWriteArraySet<String> discoveryHealthNodes;

    public static void init (String master, boolean isMaster) {
        if (discoveryNodes != null || discoveryHealthNodes != null) {
            return;
        }
        discoveryNodes = new ConcurrentHashMap<>();
        discoveryHealthNodes = new CopyOnWriteArraySet<>();
        if (isMaster) {
            return;
        }
        discoveryNodes.put(master, NODE_HEALTHY);
        discoveryHealthNodes.add(master);
    }

    /**
     * 获取健康的实例列表
     */
    public static Set<String> getHealthNodeList() {
        return new HashSet<>(discoveryHealthNodes);
    }

    /**
     * 获取用来交换的实例信息
     */
    public static Map<String, Integer> getExchangeInfo() {
        return new HashMap<>(discoveryNodes);
    }

    /**
     * 接受交换信息
     * <p>
     * 获取到的节点信息为健康且当前节点不存在该节点信息，接写入节点列表
     * 获取到的节点信息为异常，当前节点不存在该节点信息，则忽略
     * 获取到的节点信息为异常， 当前节点存在该节点信息，且为正常，则立即触发探测，当前节点对应的信息也为异常则忽略
     */
    public static void acceptExchangeInfo(Map<String, Integer> info) {
        if (info.isEmpty()) {
            return;
        }
        Set<String> exceptionList = new HashSet<>();
        info.forEach((k,v) -> {
            // 健康实例加入列表
            if (NODE_HEALTHY.equals(v)) {
                Integer status = discoveryNodes.putIfAbsent(k, NODE_HEALTHY);
                if (NODE_HEALTHY.equals(status)) {
                    discoveryHealthNodes.add(k);
                }
            } else {
                // 如果从别的节点获取到某个节点有异常，自己这边还是正常的，则立即触发检查
                if (NODE_HEALTHY.equals(discoveryNodes.get(k))) {
                    exceptionList.add(k);
                }
            }
        });
        // 检查异常
        NodeExceptionDetect.detect(exceptionList);
    }

    public static void nodeUp(String node) {
        discoveryNodes.put(node, NODE_HEALTHY);
        discoveryHealthNodes.add(node);
    }

    public static void nodeDown(String node) {
        discoveryNodes.remove(node);
        discoveryHealthNodes.remove(node);
    }


    public static void nodeExchangeFail(String node) {
        discoveryHealthNodes.remove(node);
        discoveryNodes.put(node, NODE_EXCEPTION);
        NodeExceptionDetect.detect(node);
    }











}
