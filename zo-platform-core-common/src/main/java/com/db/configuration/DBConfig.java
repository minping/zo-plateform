package com.db.configuration;

import com.jfinal.plugin.constant.DBConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
@Getter
@Setter
@ToString
@Component
public class DBConfig {

    /**
     * columnName映射模式：
     * original 原样输出,与sql中定义的大小写保持一致
     * upper 大写输出
     * lower 小写输出
     * db 根据数据库特性输出
     */
    @Value("${dsfa.db.columnMap:"+ DBConstant.DBColumnMapModel.ORIGINAL+"}")
    private String columnMapModel;

    @Value("${dsfa.db.driverDir:}")
    private String driverDir;
}
