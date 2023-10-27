package org.example.service.impl;

import org.example.manager.DiscoveryNodeManager;
import org.example.service.DiscoveryNodeService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author: 边俊超
 * @Date: 2023/10/27 16:39
 */
@Service
public class DiscoveryNodeServiceImpl implements DiscoveryNodeService {

    @Override
    public Map<String, Integer> exchangeInfo(Map<String, Integer> exchangeInfo) {
        DiscoveryNodeManager.acceptExchangeInfo(exchangeInfo);
        return DiscoveryNodeManager.getExchangeInfo();
    }

}
