package com.jfinal.config;

import com.jfinal.kit.AutoSqlMappingKit;
import com.common.util.StrKit;
import com.jfinal.model._MappingKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ActiveRecordPluginConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${package.path}")
    private String packagePath;

    @Value("${package.model.path}")
    private String modelPath;

    /**
     * logger
     */
    private static final Logger log = LoggerFactory.getLogger(ActiveRecordPluginConfig.class);

    public DruidPlugin createDruidPlugin() {
        return new DruidPlugin(url, username, password,driverClassName);
    }

    @Bean
    public ActiveRecordPlugin initActiveRecordPlugin() {
        // 配置druid数据库连接池插件
        DruidPlugin druidPlugin = this.createDruidPlugin();
        // 配置ActiveRecord插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        arp.setShowSql(true);
        arp.setDevMode(true);

        log.info("=====自动初始化=======");
        // 所有映射在 MappingKit 中自动化搞定
        if (StrKit.isBlank(modelPath)){
            log.info("未配置package.model.path，将不进行自动加载实体类映射");
        }else{
            AutoGenerator autoGenerator = new AutoGenerator();
            autoGenerator.doGenerate(modelPath,this.createDruidPlugin());
            _MappingKit.mapping(arp);
        }
        if (!StrKit.isBlank(packagePath)){
            String[] packagePathArr = packagePath.split(",");
            AutoSqlMappingKit.auto(arp,packagePathArr);
        }
        // 开启插件
        druidPlugin.start();
        arp.start();
        log.info("整合ActiveRecord成功");

        return arp;
    }
}