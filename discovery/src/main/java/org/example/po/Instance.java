package org.example.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: 边俊超
 * @Date: 2023/10/8 14:30
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instance implements Serializable {

    private static final long serialVersionUID = 9797898798754111L;

    /**
     * 服务名
     */
    private String name;
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
     * 状态    0123 =3不健康 >3将被剔除
     */
    private Integer status;
    /**
     * 是否健康
     */
    private Integer healthy;
    /**
     * 实例模式    dev test prod
     */
    private Integer mode;

    /**
     *不健康实例是否要被移除
     */
    private Integer remove;

}
