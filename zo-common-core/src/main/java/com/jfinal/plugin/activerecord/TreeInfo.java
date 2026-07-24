package com.jfinal.plugin.activerecord;

/**
 * @author zhoump
 * @date 2026/6/24
 * @purpose
 */
public class TreeInfo {

    /**
     * 子节点唯一标识
     */
    private String idKey;
    /**
     * 父节点唯一标识
     */
    private String pIdKey;
    /**
     * childKey 子数据集对应的key值
     */
    private String childKey = "children";
    /**
     * 虚拟层级字段
     * 用来描述当前结果集中的层级
     */
    private String levelKey = "_level";

    public TreeInfo(String idKey, String pIdKey) {
        this.idKey = idKey;
        this.pIdKey = pIdKey;
    }

    public String getIdKey() {
        return idKey;
    }

    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    public String getpIdKey() {
        return pIdKey;
    }

    public void setpIdKey(String pIdKey) {
        this.pIdKey = pIdKey;
    }

    public String getChildKey() {
        return childKey;
    }

    public String getLevelKey() {
        return levelKey;
    }

    public void setChildKey(String childKey) {
        this.childKey = childKey;
    }

    public void setLevelKey(String levelKey) {
        this.levelKey = levelKey;
    }
}
