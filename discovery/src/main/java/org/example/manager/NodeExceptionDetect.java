package org.example.manager;

import cn.hutool.core.thread.NamedThreadFactory;
import com.alibaba.fastjson2.JSONObject;
import org.example.runner.ExchangeNodeInfo;
import org.example.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.*;


/**
 * @Author: 边俊超
 * @Date: 2023/10/30 11:53
 */
public class NodeExceptionDetect {

//    private final static CopyOnWriteArraySet<String> exceptionList = new CopyOnWriteArraySet();

    private static Logger logger = LoggerFactory.getLogger(NodeExceptionDetect.class);


    private static final CopyOnWriteArrayList<String> detectList = new CopyOnWriteArrayList<>();

    private static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(
            1, new NamedThreadFactory("node_manager_detect_thread_", false));

    public static void detect(Set<String> nodes) {
        nodes.forEach(NodeExceptionDetect::detect);
    }

    public static void detect(String node) {
        if (detectList.addIfAbsent(node)) {
            scheduledExecutor.execute(() -> {
                try {
                    HttpClientUtil.doPost(ExchangeNodeInfo.getNodeExchangeUrl(node), JSONObject.toJSONString(DiscoveryNodeManager.getExchangeInfo()));
                    DiscoveryNodeManager.nodeUp(node);
                    logger.info("node {} up...", node);
                    detectList.remove(node);
                } catch (Exception e) {
                    logger.error("node {} detect failed...", node);
                    scheduledExecutor.schedule(()->{
                        try {
                            HttpClientUtil.doPost(ExchangeNodeInfo.getNodeExchangeUrl(node), JSONObject.toJSONString(DiscoveryNodeManager.getExchangeInfo()));
                            DiscoveryNodeManager.nodeUp(node);
                            logger.info("node {} up...", node);
                        } catch (Exception ex) {
                            logger.error("node {} detect failed...", node);
                            DiscoveryNodeManager.nodeDown(node);
                            logger.info("node {} down...", node);
                        }
                        detectList.remove(node);
                    },30, TimeUnit.SECONDS);
                }

            });
        }
    }


}
