package org.example.runner;

import com.alibaba.fastjson2.JSONObject;
import org.example.manager.DiscoveryNodeManager;
import org.example.po.Result;
import org.example.service.DiscoveryNodeService;
import org.example.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: 边俊超
 * @Date: 2023/10/27 16:49
 */
@Component
public class ExchangeNodeInfo implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${discovery.cluster.live.host}")
    private String host;

    @Value("${discovery.cluster.live.master}")
    private String master;

    @Autowired
    private DiscoveryNodeService discoveryNodeService;
    /**
     * 节点上线时，和其他节点互相交换节点列表信息，并定时交换
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        List<String> healthNodeList = DiscoveryNodeManager.getHealthNodeList();
        healthNodeList.remove(host);
        if (!host.equals(master)) {
            healthNodeList.add(master);
        }
        Map<String, Integer> exchangeInfo = DiscoveryNodeManager.getExchangeInfo();
        exchangeInfo.put(host, 1);
        Map<String, Object> params = new HashMap<>(exchangeInfo);
        healthNodeList.forEach(k -> {
            String url = "http://" + k + "/cd/node/exchange/info";
            try {
                String s = HttpClientUtil.doPost(url, params);
                Result<Map<String, Object>> result = JSONObject.parseObject(s, Result.class);
                Map<String, Object> data = result.getData();
                System.out.println(data);
                System.out.println(s);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

    }
}
