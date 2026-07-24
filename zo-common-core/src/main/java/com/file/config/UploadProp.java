package com.file.config;

import com.common.configuration.SpringContextHolder;
import com.common.util.DateKit;
import com.common.util.FileKit;
import com.common.util.StrKit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.nio.ch.FileKey;

import java.io.File;
import java.util.*;

/**
 * @author zhoump
 * @date 2026/7/16
 * @purpose
 */
@Component
public class UploadProp {

    /**
     * 虚拟目录地址
     */
    @Value("${zode.file.path:}")
    private String virtualPath;
    @Value("${zode.file.private-path:}")
    private String virtualPrivatePath;
    private String virtualStaticPath;
    /**
     * 上传文件根的相对虚拟目录的路径 必须/开头 /结尾
     */
    private String uploadBasePath = "/upload/";
    private String uploadPrivateBasePath = "/private/";
    private String staticBasePath = "/pc/static/";

    /**
     * 下载文件 多文件下载时，压缩文件内还原源文件名字缓存目录
     */
    private String downloadCacheBasePath = "/download/cache/";

    @Value("#{'${zode.file.upload-virtual-path:}'.equals('')}")
    private boolean isNonVirtualPath;
    @Value("#{'${zode.file.upload-virtual-private-path:}'.equals('')}")
    private boolean isNonVirtualPrivatePath;
    @Value("#{'${zode.file.allow:txt,doc,docx,pdf,pptx,ppt,xls,xlsx,wps,jpg,png,gif,mp4,jpeg,mp3}'.split(',')}")
    private List<String> allowFileList;
    @Value("${zode.meta.file.path:}")
    private String metaFilePath;

    private Set<String> allowFileSet;


    public String getVirtualPath() {
        if (StrKit.isBlank(this.virtualPath)) {
            this.virtualPath = SpringContextHolder.webRootDir();
        }
        return virtualPath;
    }

    public String getUploadPath() {
        if (StrKit.isBlank(this.virtualPath)) {
            this.virtualPath = SpringContextHolder.webRootDir();
        }
        if (!FileKit.exists(this.virtualPath)){
            // 创建目录（包括所有不存在的父目录）
            boolean result = new File(this.virtualPath).mkdirs();
            if (result) {
                System.out.println("目录创建成功！");
            } else {
                System.out.println("目录创建失败！");
            }
        }
        // 添加日期目录
        String dateStr = DateKit.formatDate2Str(new Date(),DateKit.YYYYMMDD);
        String uploadPath = this.virtualPath + File.separator + dateStr;
        if (!FileKit.exists(uploadPath)){
            // 创建目录（包括所有不存在的父目录）
            boolean result = new File(uploadPath).mkdirs();
            if (result) {
                System.out.println("目录创建成功！");
            } else {
                System.out.println("目录创建失败！");
            }
        }
        return uploadPath;
    }

    public String getMetaFilePath() {
        return StrKit.isBlank(this.metaFilePath) ? SpringContextHolder.webRootDir() : this.metaFilePath;
    }

    public String getVirtualStaticPath(String namespace, String pageName) {
        return getMetaFilePath() + File.separator + staticBasePath + namespace + "." + pageName;
    }

    public String getStaticBasePath() {
        return staticBasePath;
    }

    public String getStaticBasePath(String namespace,String pageName) {
        return String.format("%s%s.%s/", staticBasePath, namespace, pageName);
    }

    public String getUploadBasePath() {
        return this.uploadBasePath;
    }

    public String getUploadPrivateBasePath() {
        return this.uploadPrivateBasePath;
    }

    public String getDownloadCacheBasePath() {
        return this.downloadCacheBasePath;
    }

    public String getServerUrl() {
        String serverRootPath = SpringContextHolder.serverRootPath();
        if (serverRootPath.endsWith("/")) {
            serverRootPath = serverRootPath.substring(0, serverRootPath.length() - 1);
        }
        return serverRootPath;
    }


    public boolean isNonVirtualPath() {
        return isNonVirtualPath;
    }

    /**
     * 允许上传文件类型白名单
     *
     * @param suffix
     * @return
     */
    public boolean isAllowFile(String suffix) {
        if (this.allowFileList != null && this.allowFileList.size() > 0) {
            if (this.allowFileSet == null) {
                this.allowFileSet = new HashSet<>();
                //大文件断点续传，前端所有文件切片后为blob，使程序默认支持blob类型
                this.allowFileSet.add("blob");
                for (String allowFile : this.allowFileList) {
                    //如果没配置白名单 默认会有一个长度为1的空字符串集合。
                    if (StrKit.isBlank(allowFile) && this.allowFileList.size() == 1) {
                        this.allowFileList = new ArrayList<>();
                        return true;
                    } else {
                        this.allowFileSet.add(allowFile.toLowerCase(Locale.CHINA));
                    }
                }
            }
            return this.allowFileSet.contains(suffix.toLowerCase(Locale.CHINA));
        }
        return true;
    }
}
