package com.jfinal.kit;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReUtil;
import com.jfinal.fun.Medium;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.template.source.FileSource;
import com.jfinal.template.source.ISource;
import com.jfinal.template.source.StringSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AutoSqlMappingKit {
    private static final Logger log = LoggerFactory.getLogger(AutoSqlMappingKit.class);
    /**
     * 默认路径是项目的相对路径
     */
    public static void auto(ActiveRecordPlugin ds) {
        if (isWinOS()) {
            auto(ds, "/");
        } else {
            //获取项目跟路径
            String webRootPath = AutoSqlMappingKit.class.getResource("/").getPath();
            //获取项目绝对根路径
            Path absoluteWebPath = Paths.get(webRootPath, "/");
            auto(ds, absoluteWebPath.toString());
        }
    }

    public static void auto(ActiveRecordPlugin arp, String... packgeName) {
        auto(arp, new HashSet<>(Arrays.asList(packgeName)));
    }

    /**
     * 扫描jar包中的sql文件
     *
     * @param arp
     */
    public static void auto(ActiveRecordPlugin arp, Set<String> packageName) {
        debug("开始映射包路径{} sql文件", packageName.toString());
        ScanKit.search(packageName, o -> true, new Medium<String, ISource>() {
            @Override
            public boolean test(String s) {
                return StringKit.endsWithIgnoreCase(s, ".sql");
            }

            @Override
            public ISource map(String s) {
                try {
                    debug("映射sql文件:" + s);
                    if (s.startsWith(File.separator) || s.contains(":")) {
                        return new FileSource(null, s, Charset.defaultCharset().toString());
                    }
                    StringSource source = new StringSource(IoUtil.read(new InputStreamReader(this.getClass().getResourceAsStream("/" + s), "UTF-8")), true);
                    if (ReUtil.isMatch(".*#namespace.*#end.*", source.getContent().toString())) {
                        return source;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void apply(ISource stringSource) {
                if (stringSource != null) {
                    arp.addSqlTemplate(stringSource);
                }
            }
        });

    }

    private static void debug(String msg, Object... args) {
        log.debug(msg, args);
    }

    public static boolean isWinOS() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return true;
        } else {
            return false;
        }
    }
}
