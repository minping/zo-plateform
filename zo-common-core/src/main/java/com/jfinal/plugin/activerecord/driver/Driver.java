package com.jfinal.plugin.activerecord.driver;


import com.common.util.StrKit;
import com.jfinal.plugin.activerecord.exception.Code;
import com.jfinal.plugin.constant.DBConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;

/**
 * @author wenjin
 * @date 2024/4/16 14:14
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@Slf4j
public class Driver {

    /**
     * 驱动完整类名
     */
    private String fullName;

    /**
     * 驱动版本信息
     */
    private String version;

    /**
     * 驱动jar包目录
     * 多个用逗号分隔
     */
    private String[] jarDirs;

    /**
     * 驱动classLoader
     * 默认com.dsfa.platform.starter.db.jfinal.plugin.activerecord.cl.JarClassLoader
     */
    private String classLoader;

    /**
     * 是否匹配
     * @param fullName
     * @param version
     * @return
     */
    public boolean match(String fullName,String version){
        return StrKit.equals(fullName,this.fullName) && StrKit.equals(version,this.version);
    }

    public ClassLoader getClassLoaderInstance(String driverBaseDir){
        String cpClassLoader = DBConstant.DEFAULT_DRIVER_CLASS_LOADER;
        if(StrKit.isNotBlank(classLoader)){
            cpClassLoader = this.classLoader;
        }
        try {
            Class<?> aClass = Class.forName(cpClassLoader);
            Constructor<?> constructor = aClass.getConstructor(String[].class);
            String[] fullJarDirs = new String[this.jarDirs.length];
            for (int i = 0; i < this.jarDirs.length; i++) {
                fullJarDirs[i] = driverBaseDir+"/"+this.jarDirs[i];
            }
            ClassLoader classLoaderInstance = (ClassLoader) constructor.newInstance((Object) fullJarDirs);
            return classLoaderInstance;
        } catch (Exception e) {
            log.error("driver classloader ["+cpClassLoader+"] new instance error",e);
            Code.DRIVER_CLASSLOADER_ERROR.doThrow();
        }
        return null;
    }

}
