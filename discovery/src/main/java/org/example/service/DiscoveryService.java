package org.example.service;

import org.example.po.Keepalive;
import org.example.po.Register;

/**
 * @Author: 边俊超
 * @Date: 2023/10/8 15:36
 */
public interface DiscoveryService {

    /**
     * 实例注册
     * @param register
     */
    String register(Register register);

    /**
     * 心跳
     * @param keepalive
     */
    void keepalive(Keepalive keepalive);
}
