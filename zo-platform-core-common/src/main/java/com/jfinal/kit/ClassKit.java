package com.jfinal.kit;

/**
 * @author zhoump
 * @date 2026/6/24
 * @purpose
 */
public class ClassKit {

    /**
     * 比较两个class是否一致，支持父类class
     * @param targetClass
     * @param compClass
     * @return
     */
    public static Boolean equalsClass(Class targetClass, Class compClass){
        if(targetClass == Object.class){
            return targetClass == compClass;
        }
        return targetClass == compClass ? true : equalsClass(targetClass.getSuperclass(),compClass);
    }
}
