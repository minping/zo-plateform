package com.common.base;

import com.common.views.Mv;
import com.jfinal.plugin.activerecord.Record;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.*;

/**
 * @author zhoump
 * @date 2026/5/18
 * @purpose
 */
public class BaseController  extends Base {


    public String getPara(String name) {
        return request().getParameter(name);
    }

    public Record getParaToRecord(String... names) {
        Record record = new Record();
        Map properties = request().getParameterMap();
        Iterator entries = properties.entrySet().iterator();
        String name = null;
        String value = null;
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (valueObj == null) {
                value = "";
            } else if ((valueObj instanceof String[])) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            record.set(name, value);
        }
        return record;
    }

    /**
     * 是否为上传请求
     *
     * @return
     */
    public boolean isUploadReq() {
        return StringUtils.startsWithIgnoreCase(request().getContentType(), "multipart/");
    }


    ///**
    // * 获取单个上传文件
    // *
    // * @return
    // */
    //public FileModel getFile() {
    //    List<FileModel> fileModelList = getFileList();
    //    if (fileModelList != null && fileModelList.size() > 0) {
    //        return fileModelList.get(0);
    //    }
    //    return null;
    //}
    //
    ///**
    // * 获取单个文件
    // * 如果key对应多个，默认取第一个文件
    // *
    // * @param reqKey
    // * @return
    // */
    //public FileModel getFile(String reqKey) {
    //    if (isUploadReq()) {
    //        List<FileModel> fileModelList = getFiles(reqKey);
    //        if (fileModelList != null) {
    //            return fileModelList.get(0);
    //        }
    //        return null;
    //    } else {
    //        ErrorCode.FILE_UPLOAD_ERROR.doThrow();
    //        return null;
    //    }
    //}
    //
    ///**
    // * 获取对应上传控件key的所有文件
    // *
    // * @param reqKey
    // * @return
    // */
    //public List<FileModel> getFiles(String reqKey) {
    //    if (isUploadReq()) {
    //        MultipartHttpServletRequest multipartRequest = multipartHttpServletRequest();
    //        List<MultipartFile> multipartFileList = multipartRequest.getFiles(reqKey);
    //        if (multipartFileList != null && multipartFileList.size() > 0) {
    //            List<FileModel> fileModelList = new ArrayList<>(multipartFileList.size());
    //            for (MultipartFile multipartFile : multipartFileList) {
    //                fileModelList.add(new FileModel(multipartFile));
    //            }
    //            return fileModelList;
    //        }
    //        return null;
    //    } else {
    //        ErrorCode.FILE_UPLOAD_ERROR.doThrow();
    //        return null;
    //    }
    //}
    //
    ///**
    // * 获取所有上传文件
    // *
    // * @return
    // */
    //public List<FileModel> getFileList() {
    //    if (isUploadReq()) {
    //        MultipartHttpServletRequest multipartRequest = multipartHttpServletRequest();
    //        MultiValueMap<String, MultipartFile> multipartFileMap = multipartRequest.getMultiFileMap();
    //        if (multipartFileMap != null && multipartFileMap.size() > 0) {
    //            List<FileModel> fileModelList = new ArrayList<>();
    //            for (String key : multipartFileMap.keySet()) {
    //                List<MultipartFile> multipartFileList = multipartFileMap.get(key);
    //                for (MultipartFile multipartFile : multipartFileList) {
    //                    fileModelList.add(new FileModel(multipartFile));
    //                }
    //            }
    //            return fileModelList;
    //        }
    //        return null;
    //    } else {
    //        ErrorCode.FILE_UPLOAD_ERROR.doThrow();
    //        return null;
    //    }
    //}
    //
    ///**
    // * 获取所有文件
    // * key: 前端上传控件的name 一个上传控件对应可多个上传文件
    // *
    // * @return
    // */
    //public Map<String, List<FileModel>> getFileMap() {
    //    if (isUploadReq()) {
    //        MultipartHttpServletRequest multipartRequest = multipartHttpServletRequest();
    //        MultiValueMap<String, MultipartFile> multipartFileMap = multipartRequest.getMultiFileMap();
    //        if (multipartFileMap != null && multipartFileMap.size() > 0) {
    //            Map<String, List<FileModel>> fileModelMap = new HashMap<>();
    //            for (String key : multipartFileMap.keySet()) {
    //                List<MultipartFile> multipartFileList = multipartFileMap.get(key);
    //                List<FileModel> fileModelList = new ArrayList<>(multipartFileList.size());
    //                for (MultipartFile multipartFile : multipartFileList) {
    //                    fileModelList.add(new FileModel(multipartFile));
    //                }
    //                fileModelMap.put(key, fileModelList);
    //            }
    //            return fileModelMap;
    //        }
    //        return null;
    //    } else {
    //        ErrorCode.FILE_UPLOAD_ERROR.doThrow();
    //        return null;
    //    }
    //}


    /**
     * @return ModelAndView
     */
    public Mv mv() {
        return Mv.create();
    }

    public Mv redirect(String url) {
        return mv().fluentSetViewName("redirect:" + url);
    }

    /**
     * ModelAndView
     *
     * @param viewName
     * @return
     */
    public Mv mv(String viewName) {
        return mv().fluentSetViewName(viewName);
    }
}
