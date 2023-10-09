package org.example.service;

import org.example.po.Keepalive;
import org.example.po.Register;
import org.example.po.Subscribe;

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

    /**
     * 注销
     * @param keepalive
     */
    void cancel(Keepalive keepalive);

    /**
     * 订阅
     * @param subscribe
     * @return
     */
    Object subscribe(Subscribe subscribe);
}
