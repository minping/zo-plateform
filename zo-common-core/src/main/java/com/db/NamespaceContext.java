package com.db;

import org.springframework.core.annotation.AnnotatedElementUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoump
 * @date 2026/6/24
 * @purpose
 */
public class NamespaceContext {
    private Map<Class,String> namespaceMap = new HashMap<>();

    private static volatile  NamespaceContext namespaceContext;

    private NamespaceContext(){

    }

    public static NamespaceContext newInstance(){
        if(namespaceContext==null){
            synchronized (NamespaceContext.class){
                if(namespaceContext == null){
                    namespaceContext = new NamespaceContext();
                    return namespaceContext;
                }
            }
        }
        return namespaceContext;
    }

    public String getNamespace(Class c){
        String namespace = namespaceMap.get(c);
        if(namespace==null){
            synchronized (c){
                Namespace ns = AnnotatedElementUtils.findMergedAnnotation(c,Namespace.class);
                if(ns==null){
                    throw new RuntimeException("没有定义Namespace");
                }
                namespace = ns.value();
                namespaceMap.put(c,namespace);
            }
        }
        return namespace;
    }
}
