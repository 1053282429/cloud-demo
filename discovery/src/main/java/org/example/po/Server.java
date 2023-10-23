package org.example.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: 边俊超
 * @Date: 2023/10/8 14:46
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Server implements Serializable {

    private static final long serialVersionUID = 121344575454544121L;

    /**
     * 服务名
     */
    private String name;

    /**
     * 实例列表
     */
    private List<Instance> instances;

    /**
     * 服务是否可用。当实例列表不为空，且健康实例数大于1时可用。
     */
    private Integer available;


}
