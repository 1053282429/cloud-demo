package org.example.po;

import java.util.List;

/**
 * @Author: 边俊超
 * @Date: 2023/10/8 15:29
 */
public class Subscribe {

    /**
     * 回调通知地址
     */
    private String callbackUrl;

    /**
     * 订阅所有服务
     */
    private Integer subAll;

    /**
     * 订阅的服务名
     */
    private List<String> subServerName;

    /**
     * 只订阅健康实例
     */
    private Integer subHealthy;


}
