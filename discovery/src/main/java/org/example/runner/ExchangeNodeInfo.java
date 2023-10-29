package org.example.runner;

import cn.hutool.core.thread.NamedThreadFactory;
import com.alibaba.fastjson2.JSONObject;
import org.example.manager.DiscoveryNodeManager;
import org.example.po.Result;
import org.example.service.DiscoveryNodeService;
import org.example.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: 边俊超
 * @Date: 2023/10/27 16:49
 */
@Component
public class ExchangeNodeInfo implements ApplicationListener<ContextRefreshedEvent> {

    Logger logger = LoggerFactory.getLogger(ExchangeNodeInfo.class);


    @Value("${discovery.cluster.live.host}")
    private String host;

    @Value("${discovery.cluster.live.master}")
    private String master;

    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(
            1, new NamedThreadFactory("node_manager_scheduled_thread_", false));

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1,
            Integer.MAX_VALUE,
            60,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new NamedThreadFactory("node_manager_communication_thread_", false));


    @Autowired
    private DiscoveryNodeService discoveryNodeService;
    /**
     * 节点上线时，和其他节点互相交换节点列表信息，并定时交换
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (host.equals(master)) {
                return;
            }
            Map<String, Object> params = new HashMap<>();
            params.put(host, 1);
            String url = "http://" + master + "/cd/node/exchange/info";
            String s = null;
            try {
                s = HttpClientUtil.doPost(url, JSONObject.toJSONString(params));
            } catch (Exception e) {
                logger.error("master connection failed...");
            }
            Result<Map<String, Integer>> result = JSONObject.parseObject(s, Result.class);
            if (result != null) {
                Map<String, Integer> data = result.getData();
                data.remove(host);
                DiscoveryNodeManager.acceptExchangeInfo(data);
            }
        } finally {
            scheduledExecutor.schedule(new ExchangeInfo(), 30, TimeUnit.SECONDS);
        }
    }

    class ExchangeInfo implements Runnable {
        @Override
        public void run() {

            List<String> healthNodeList = DiscoveryNodeManager.getHealthNodeList();
            if (healthNodeList.isEmpty()) {
                return;
            }
            healthNodeList.forEach(address -> {

            });

            DiscoveryNodeManager.getHealthNodeList().forEach(
                    address -> executor.execute(() -> {
                        String url = "http://" + address + "/cd/node/exchange/info";
                        String s;
                        Map<String, Integer> exchangeInfo = DiscoveryNodeManager.getExchangeInfo();
                        try {
                            s = HttpClientUtil.doPost(url, JSONObject.toJSONString(exchangeInfo));
                        } catch (Exception e) {
                            logger.error("node connection failed...");
                            throw new RuntimeException(e);
                        }
                        Result<Map<String, Integer>> result = JSONObject.parseObject(s, Result.class);
                        Map<String, Integer> data = result.getData();
                        data.remove(host);
                        DiscoveryNodeManager.acceptExchangeInfo(data);
                    })
            );
            scheduledExecutor.schedule(new ExchangeInfo(), 30, TimeUnit.SECONDS);
        }
    }


}
