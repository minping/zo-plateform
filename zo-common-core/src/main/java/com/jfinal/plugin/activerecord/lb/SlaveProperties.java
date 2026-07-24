package com.jfinal.plugin.activerecord.lb;

import com.jfinal.plugin.druid.DruidPlugin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 负载配置信息
 * @author wenjin
 * @date 2023/10/24 21:14
 */
@Getter
@Setter
@NoArgsConstructor
public class SlaveProperties {

    /**
     * 负载策略
     */
    private LoadBalancerStrategy lb;
    /**
     * 从库权重，与List<DruidPlugin> slaves 数量一致
     */
    private Integer[] weight;

    /**
     * 从库
     */
    private List<DruidPlugin> datasource;
}
