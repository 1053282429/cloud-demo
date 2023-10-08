package org.example.po;

import lombok.Data;

/**
 * @author 10532
 */
@Data
public class Keepalive {

    /**
     * token
     */
    private String token;

    /**
     * 服务名
     */
    private String name;

    /**
     * 服务分组
     */
    private String group;
}
