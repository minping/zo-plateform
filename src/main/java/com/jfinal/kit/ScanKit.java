package com.jfinal.kit;

import com.jfinal.fun.Medium;
import com.google.common.collect.Sets;
import com.jfinal.holder.DBSpringContextHolder;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public final class ScanKit {

    static <R> void fileMedium(Set<R> container, Medium<String, R> fileMedium, String path) {
        if (fileMedium == null) {
            container.add((R) path);
        } else if (fileMedium.test(path)) {
            R r = fileMedium.map(path);
            container.add(r);
            fileMedium.apply(r);
        }
    }

    static <R> void searchPath(Set<R> container, File file, Medium<String, R> fileMedium) {
        String path = file.getPath();
        if (!file.isDirectory()) {
            fileMedium(container, fileMedium, path);
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                searchPath(container, f, fileMedium);
            }
        }

    }

    static <R> void searchJar(Set<R> container, String packageName, Medium<String, R> fileMedium, URL url) throws IOException {
        JarURLConnection connection = (JarURLConnection) url.openConnection();
        if (connection != null) {
            JarFile jarFile = connection.getJarFile();
            if (jarFile != null) {
                //jar实体
                Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                while (jarEntryEnumeration.hasMoreElements()) {
                    JarEntry entry = jarEntryEnumeration.nextElement();
                    String jarEntryName = entry.getName();
                    if (jarEntryName.startsWith(packageName)) {
                        fileMedium(container, fileMedium, jarEntryName);
                    }
                }
            }
        }
    }

    public static <R> Set<R> search(Set<String> packageName) {
        return search(packageName, null);
    }

    public static <R> Set<R> search(Set<String> packageName, Medium<String, R> proFilter) {
        return search(packageName, proFilter, null);
    }

    /**
     * 获取文件
     * 支持目录文件和jar包中的文件
     *
     * @param packageName 包路径
     * @param proFilter   包过滤
     * @param fileMedium  文件处理器
     * @param <R>
     * @return
     */
    public static <R> Set<R> search(Set<String> packageName, Medium<String, R> proFilter, Medium<String, R> fileMedium) {
        Set<R> container = new HashSet<>();
        // 子父级去重
        Sets.newHashSet(packageName).forEach(r -> {
            packageName.removeAll(packageName.stream().filter(sr -> !r.equals(sr) && sr.startsWith(r + ".")).collect(Collectors.toSet()));
        });

        packageName.stream().map(n -> n.replace(".", "/")).forEach(n -> {
            try {
                Enumeration<URL> urlEnumeration = Thread.currentThread().getContextClassLoader().getResources(n);
                while (urlEnumeration.hasMoreElements()) {
                    URL url = urlEnumeration.nextElement();//jar:file:<path>!/packgeName;<path>/packgeName
                    String protocol = url.getProtocol();
                    if (proFilter == null || proFilter.test(protocol)) {
                        switch (protocol.toLowerCase()) {
                            case "file":
                                searchPath(container, new File(url.getFile()), fileMedium);
                                break;
                            case "jar":
                                searchJar(container, n, fileMedium, url);
                                break;
                            default:
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return container;
    }

    static Set<String> getScanPackage(Object scanProxy, Medium<String, String> fileMedium) {
        String proxyClassName = scanProxy.getClass().getName();
        String targetName = proxyClassName.substring(0, proxyClassName.indexOf("$$"));
        try {
            Object target = ReflectKit.newInstance(Class.forName(targetName));
            SpringBootApplication scan = target.getClass().getAnnotation(SpringBootApplication.class);
            Set<String> result;
            if (scan == null) {
                //无配置ComponentScan 默认当前启动类所在包
                result = new HashSet<>(Arrays.asList(targetName.substring(0, targetName.lastIndexOf("."))));
            } else {
                result = Arrays.stream(scan.scanBasePackages()).collect(Collectors.toSet());
            }
            if (fileMedium != null) {
                return result.stream()
                        .filter(fileMedium::test)
                        .map(fileMedium::map)
                        .peek(fileMedium::apply)
                        .collect(Collectors.toSet());
            }
            return result;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取spring管理扫描包路径
     *
     * @return
     */
    public static Set<String> scanSearch() {
        return scanSearch(null);
    }

    public static Set<String> scanSearch(Medium<String, String> fileMedium) {
        Set<String> result = new HashSet<>();
        Map<String, Object> scanMap = DBSpringContextHolder.getBeansWithAnnotation(SpringBootApplication.class);
        for (Object scan : scanMap.values()) {
            result.addAll(getScanPackage(scan, fileMedium));
        }
        return result;
    }
}
