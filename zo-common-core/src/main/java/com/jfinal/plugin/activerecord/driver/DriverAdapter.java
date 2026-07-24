package com.jfinal.plugin.activerecord.driver;

import com.common.util.FileKit;
import com.common.util.StrKit;
import com.db.configuration.DBConfig;
import com.jfinal.holder.DBSpringContextHolder;
import com.jfinal.kit.YamlKit;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class DriverAdapter {


    public static final String DRIVER_PATH = "/META-INF/db.yaml";

    private static DriverContext driverContext;

    /**
     * 默认jar同级 driver目录
     */
    private static String driverBaseDir;
    /**
     * 获取驱动上线文
     */
    public static DriverContext getDriverContext(){
        if(driverContext==null){
            synchronized (DriverAdapter.class){
                if(driverContext==null){
                    String content = FileKit.readInClass(DRIVER_PATH);
                    if(StrKit.isNotBlank(content)){
                        driverContext = YamlKit.readValue(content, DriverContext.class);
                        DBConfig dbConfig = DBSpringContextHolder.getBean(DBConfig.class);
                        String driverDir = dbConfig.getDriverDir();
                        if(StrKit.isBlank(driverDir)){
                            //获取当前jar包同级目录
                            Path jarPath = null;
                            try {
                                jarPath = Paths.get(DriverAdapter.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                            // 获取当前 Jar 包的父目录
                            driverDir = jarPath.getParent().toString();
                            driverDir = driverDir+"/driver";
                        }
                        driverBaseDir = driverDir;
                    }else{
                        driverContext = new DriverContext();
                    }
                }
            }
        }
        return driverContext;
    }




    /**
     * 获取driver classLoader
     * @param driverFullClass
     * @param version
     * @return
     */
    public static ClassLoader getClassLoader(String driverFullClass,String version){
        Driver driver = getDriverContext().getDriver(driverFullClass, version);
        if(driver!=null){
            return driver.getClassLoaderInstance(driverBaseDir);
        }
        return null;
    }

}
