package org.example.po;

import lombok.Data;

/**
 * @Author: 边俊超
 * @Date: 2023/10/8 15:27
 */
@Data
public class Register {

    /**
     * 服务名
     */
    private String name;
    /**
     * 业务分组
     */
    private String group;
    /**
     * ip
     */
    private String ip;
    /**
     * port
     */
    private Integer port;
    /**
     * mac地址
     */
    private String macAddress;
    /**
     * 实例模式    dev test prod
     */
    private Integer mode;

    /**
     *不健康实例是否要被移除
     */
    private Integer remove;

}
