package org.example.runner;

import cn.hutool.core.thread.NamedThreadFactory;
import com.alibaba.fastjson2.JSONObject;
import org.example.manager.DiscoveryNodeManager;
import org.example.po.Result;
import org.example.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
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

    /**
     * 节点上线时，和其他节点互相交换节点列表信息，并定时交换
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        DiscoveryNodeManager.init(master, master.equals(host));
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            Set<String> healthNodeList = DiscoveryNodeManager.getHealthNodeList();
            healthNodeList.forEach(address -> executor.execute(() -> {
                String s = null;
                try {
                    Map<String, Integer> exchangeInfo = DiscoveryNodeManager.getExchangeInfo();
                    exchangeInfo.put(host, DiscoveryNodeManager.NODE_HEALTHY);
                    s = HttpClientUtil.doPost(getNodeExchangeUrl(address), JSONObject.toJSONString(exchangeInfo));
                } catch (Exception e) {
                    DiscoveryNodeManager.nodeExchangeFail(address);
                    logger.error("node {} connection failed...", address);
                }
                if (s == null) {
                    return;
                }
                Result<Map<String, Integer>> result = JSONObject.parseObject(s, Result.class);
                if (result == null) {
                    return;
                }
                Map<String, Integer> data = result.getData();
                if (data == null) {
                    return;
                }
                data.remove(host);
                DiscoveryNodeManager.acceptExchangeInfo(data);
            }));
        },0,30,TimeUnit.SECONDS);
    }

    public static String getNodeExchangeUrl(String node) {
        return "http://" + node + "/cd/node/exchange/info";
    }

}
