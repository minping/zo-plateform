package com.jfinal.config;

import com.db.druid.IDataSourceDefinition;
import com.jfinal.holder.DBSpringContextHolder;
import com.jfinal.kit.AutoSqlMappingKit;
import com.common.util.StrKit;
import com.jfinal.kit.ScanKit;
import com.jfinal.model._MappingKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.serializer.RecordJsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


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
        // 开启插件
        druidPlugin.start();

        if (DbKit.getConfig(druidPlugin.getName()) != null) {
            log.debug("Druid {} 配置已存在", druidPlugin.getName());
        }
        String dataSourceBeanName = DbKit.getDataSourceBeanName(druidPlugin.getName());
        DataSource dataSource = DBSpringContextHolder.containsBean(dataSourceBeanName) ?
                (DataSource) DBSpringContextHolder.getBean(dataSourceBeanName) : null;
        if (dataSource == null) {
            //注册数据源bean
            DBSpringContextHolder.registerSingleton(dataSourceBeanName, druidPlugin.getDataSource());
            dataSource = (DataSource) DBSpringContextHolder.getBean(dataSourceBeanName);
            //注册数据源对应的事务管理器
            DBSpringContextHolder.registerBean(druidPlugin.getName(), transactionManagerBeanDefinition(druidPlugin.getName(), dataSource));
        }
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin.getName(), dataSource);
        // 配置ActiveRecord插件
        //ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        arp.setShowSql(true);
        arp.setDevMode(true);
        arp.getEngine().setDevMode(true);

        log.info("=====自动初始化=======");
        // 所有映射在 MappingKit 中自动化搞定
        if (StrKit.isBlank(modelPath)){
            log.info("未配置package.model.path，将不进行自动加载实体类映射");
        }else{
            log.info("自动加载实体类映射...");
            AutoGenerator autoGenerator = new AutoGenerator();
            autoGenerator.doGenerate(modelPath,this.createDruidPlugin());
            _MappingKit.mapping(arp);
        }
        //读取sql文件
        if (!StrKit.isBlank(packagePath)){
            String[] packagePathArr = packagePath.split(",");
            AutoSqlMappingKit.auto(arp,packagePathArr);
            //AutoSqlMappingKit.auto(arp, ScanKit.scanSearch());
        }

        arp.start();
        log.info("整合ActiveRecord成功");

        return arp;
    }
    //
    //@Bean
    //public RecordJsonSerializer recordJsonSerializer(){
    //    return new RecordJsonSerializer();
    //}

    private AbstractBeanDefinition transactionManagerBeanDefinition(String beanName, DataSource dataSource) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
        builder.getBeanDefinition().setAttribute("id", beanName);
        builder.addPropertyValue("dataSource", dataSource);
        return builder.getBeanDefinition();
    }
}