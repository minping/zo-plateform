package com.common.configuration;

import com.common.util.StrKit;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.ResolvableType;
import org.springframework.util.StringValueResolver;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author zhoump
 * @date 2026/5/18
 * @purpose
 */
public class SpringContextHolder implements ApplicationContextAware, EmbeddedValueResolverAware {

    private static ApplicationContext applicationContext = null;
    private static StringValueResolver stringValueResolver;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return SpringContextHolder.applicationContext.getBeansWithAnnotation(annotationType);
    }

    public static boolean containsBean(String name) {
        return SpringContextHolder.applicationContext.containsBean(name);
    }

    public static Object getBean(String s) throws BeansException {
        return SpringContextHolder.applicationContext.getBean(s);
    }


    public static <T> T getBean(String s, Class<T> aClass) throws BeansException {
        return SpringContextHolder.applicationContext.getBean(s, aClass);
    }

    public static Object getBean(String s, Object... objects) throws BeansException {
        return SpringContextHolder.applicationContext.getBean(s, objects);
    }


    public static <T> T getBean(Class<T> aClass) throws BeansException {
        return SpringContextHolder.applicationContext.getBean(aClass);
    }


    public static <T> T getBean(Class<T> aClass, Object... objects) throws BeansException {
        return SpringContextHolder.applicationContext.getBean(aClass, objects);
    }


    public static <T> ObjectProvider<T> getBeanProvider(Class<T> aClass) {
        return SpringContextHolder.applicationContext.getBeanProvider(aClass);
    }


    public static <T> ObjectProvider<T> getBeanProvider(ResolvableType resolvableType) {
        return SpringContextHolder.applicationContext.getBeanProvider(resolvableType);
    }

    public static boolean isSingleton(String s) throws NoSuchBeanDefinitionException {
        return SpringContextHolder.applicationContext.isSingleton(s);
    }


    public static boolean isPrototype(String s) throws NoSuchBeanDefinitionException {
        return SpringContextHolder.applicationContext.isPrototype(s);
    }


    public static boolean isTypeMatch(String s, ResolvableType resolvableType) throws NoSuchBeanDefinitionException {
        return SpringContextHolder.applicationContext.isTypeMatch(s, resolvableType);
    }


    public static boolean isTypeMatch(String s, Class<?> aClass) throws NoSuchBeanDefinitionException {
        return SpringContextHolder.applicationContext.isTypeMatch(s, aClass);
    }


    public static Class<?> getType(String s) throws NoSuchBeanDefinitionException {
        return SpringContextHolder.applicationContext.getType(s);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> baseType) {
        return SpringContextHolder.applicationContext.getBeansOfType(baseType);
    }


    public static String[] getAliases(String s) {
        return SpringContextHolder.applicationContext.getAliases(s);
    }

    /**
     * 动态注册bean
     *
     * @param beanName      注册的bean 名称
     * @param fullClassName 注册的bean 类完整路径
     */
    public static void registerBean(String beanName, String fullClassName) {
        try {
            registerBean(beanName, Class.forName(fullClassName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    public static void registerBean(String beanName, BeanDefinitionBuilder beanDefinitionBuilder) {
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

    private static DefaultListableBeanFactory getBeanFactory() {
        // 将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) SpringContextHolder.applicationContext;
        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext
                .getBeanFactory();
        return defaultListableBeanFactory;
    }

    public static HttpServletRequest request() {
        return requestAttributes().getRequest();
    }

    public static HttpSession session() {
        return request().getSession();
    }


    public static HttpServletResponse response() {
        return requestAttributes().getResponse();
    }

    public static ServletRequestAttributes requestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    /**
     * webRoot 根目录
     *
     * @return
     */
    public static String webRootDir() {
        return request().getSession().getServletContext().getRealPath("/");
    }

    /**
     * 获取项目根目录
     *
     * @return
     */
    public static String contextPath() {
        return request().getContextPath();
    }

    /**
     * 获取服务器请求根地址
     *
     * @return
     */
    public static String serverRootPath() {
        return getSchemeAndHost() + contextPath().replace("/", "");
    }

    /**
     * 动态获取配置文件中的值
     *
     * @param name
     * @return
     */
    public static String getPropertiesValue(String name) {
        try {
            name = "${" + name + "}";
            return stringValueResolver.resolveStringValue(name);
        } catch (Exception e) {
            // 获取失败则返回null
            return null;
        }
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        SpringContextHolder.stringValueResolver = stringValueResolver;
    }

    public static String getSchemeAndHost() {
        String origin = request().getHeader("Origin");
        if (StrKit.isBlank(origin)) {
            //无origin
            String requestURL = request().getRequestURL().toString();
            String requestURI = request().getRequestURI();
            return requestURL.replace(requestURI, "/");
        } else {
            return origin + "/";
        }
    }
}
