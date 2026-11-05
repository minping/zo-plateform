package com.jfinal.dialect.date;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class Patten {

    /**
     * 原表单式
     */
    private String source;

    /**
     * 转换后表达式
     */
    private String convert;

    public Patten(String source,String convert){
        this.source = source;
        this.convert =convert;
    }

    public static Patten by(String source,String convert){
        return new Patten(source,convert);
    }

    public String getSource() {
        return source;
    }

    public Patten setSource(String source) {
        this.source = source;
        return this;
    }

    public String getConvert() {
        return convert;
    }

    public Patten setConvert(String convert) {
        this.convert = convert;
        return this;
    }
}
