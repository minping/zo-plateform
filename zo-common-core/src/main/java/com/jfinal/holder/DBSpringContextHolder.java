package com.jfinal.holder;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

@Component
@Lazy(false)
public class DBSpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        applicationContext = appContext;
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return DBSpringContextHolder.applicationContext.getBeansWithAnnotation(annotationType);
    }

    public static boolean containsBean(String name) {
        return DBSpringContextHolder.applicationContext.containsBean(name);
    }

    public static Object getBean(String s) throws BeansException {
        return DBSpringContextHolder.applicationContext.getBean(s);
    }


    public static <T> T getBean(String s, Class<T> aClass) throws BeansException {
        return DBSpringContextHolder.applicationContext.getBean(s, aClass);
    }

    public static Object getBean(String s, Object... objects) throws BeansException {
        return DBSpringContextHolder.applicationContext.getBean(s, objects);
    }


    public static <T> T getBean(Class<T> aClass) throws BeansException {
        return DBSpringContextHolder.applicationContext.getBean(aClass);
    }


    public static <T> T getBean(Class<T> aClass, Object... objects) throws BeansException {
        return DBSpringContextHolder.applicationContext.getBean(aClass, objects);
    }


    public static <T> ObjectProvider<T> getBeanProvider(Class<T> aClass) {
        return DBSpringContextHolder.applicationContext.getBeanProvider(aClass);
    }


    public static <T> ObjectProvider<T> getBeanProvider(ResolvableType resolvableType) {
        return DBSpringContextHolder.applicationContext.getBeanProvider(resolvableType);
    }

    public static boolean isSingleton(String s) throws NoSuchBeanDefinitionException {
        return DBSpringContextHolder.applicationContext.isSingleton(s);
    }


    public static boolean isPrototype(String s) throws NoSuchBeanDefinitionException {
        return DBSpringContextHolder.applicationContext.isPrototype(s);
    }


    public static boolean isTypeMatch(String s, ResolvableType resolvableType) throws NoSuchBeanDefinitionException {
        return DBSpringContextHolder.applicationContext.isTypeMatch(s, resolvableType);
    }


    public static boolean isTypeMatch(String s, Class<?> aClass) throws NoSuchBeanDefinitionException {
        return DBSpringContextHolder.applicationContext.isTypeMatch(s, aClass);
    }


    public static Class<?> getType(String s) throws NoSuchBeanDefinitionException {
        return DBSpringContextHolder.applicationContext.getType(s);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> baseType) {
        return DBSpringContextHolder.applicationContext.getBeansOfType(baseType);
    }


    public static String[] getAliases(String s) {
        return DBSpringContextHolder.applicationContext.getAliases(s);
    }

    /**
     * 动态注册bean
     *
     * @param beanName      注册的bean 名称
     * @param fullClassName 注册的bean 类完整路径
     */
    public static void registerBean(String beanName, String fullClassName) {
        registerBean(beanName, fullClassName);
    }

    public static void registerBean(String beanName, Class cla) {
        registerBean(beanName, genericBeanDefinition(cla));
    }

    private static BeanDefinitionBuilder genericBeanDefinition(Class cla) {
        return BeanDefinitionBuilder
                .genericBeanDefinition(cla);
    }

    private static BeanDefinitionBuilder genericBeanDefinition(String fullClassName) {
        return BeanDefinitionBuilder
                .genericBeanDefinition(fullClassName);
    }

    private static void registerBean(String beanName, BeanDefinitionBuilder beanDefinitionBuilder) {
        DefaultListableBeanFactory defaultListableBeanFactory = getBeanFactory();
        if (defaultListableBeanFactory.containsBeanDefinition(beanName)) {
            defaultListableBeanFactory.removeBeanDefinition(beanName);
        }

        defaultListableBeanFactory.registerBeanDefinition(beanName,
                beanDefinitionBuilder.getRawBeanDefinition());
    }

    public static void registerBean(String beanName, AbstractBeanDefinition beanDefinition) {
        getBeanFactory().registerBeanDefinition(beanName, beanDefinition);
    }

    public static void registerSingleton(String beanName,Object singletonObj){
        getBeanFactory().registerSingleton(beanName,singletonObj);
    }

    private static DefaultListableBeanFactory getBeanFactory() {
        // 将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) DBSpringContextHolder.applicationContext;
        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext
                .getBeanFactory();
        return defaultListableBeanFactory;
    }
}