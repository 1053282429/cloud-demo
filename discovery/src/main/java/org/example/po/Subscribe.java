package org.example.po;

import lombok.Data;

import java.util.List;

/**
 * @Author: 边俊超
 * @Date: 2023/10/8 15:29
 */
@Data
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
     * 只订阅健康实例  针对subAll=1
     */
    private Integer subHealthy;

    /**
     * 订阅的服务名
     */
    private List<Server> server;


    @Data
    public static class Server {
        /**
         * 业务分组
         */
        private String group;

        /**
         * 服务名
         */
        private String name;

        /**
         * 只订阅健康实例
         */
        private Integer subHealthy;

    }


}
