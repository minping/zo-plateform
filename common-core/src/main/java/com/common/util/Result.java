package com.common.util;


import com.jfinal.plugin.activerecord.Record;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 数据返回集
 * {
 * success:true || false,//请求执行成功或者失败
 * state:20000 || 20001 || ...,//成功、失败、或其他请求状态
 * message:"成功 || 失败 || 请求结果消息",
 * data:{},//返回数据结果集
 * }
 *
 * @author feiwu E-mail: feiwuchen@outlook.com
 * @date 2018年6月27日 上午10:47:29
 * @desc
 */
public class Result extends HashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 5495497842351460243L;

    public static final int SUCCESS = 20000;
    public static final int ERROR = 20001;
    public static final Serializable EMPTY = new Serializable() {
    };


    public Result() {

    }

    ;

    public static Result me() {
        return new Result();
    }

    /**
     * 执行结果消息
     *
     * @param message
     * @return
     */
    public Result setMessage(String message) {
        this.put("message", message);
        return this;
    }

    /**
     * 执行成功 或者 错误 信息
     *
     * @param flag
     * @return
     */
    public Result setSuccess(boolean flag) {
        this.put("success", flag);
        return this;
    }

    /**
     * 执行返回结果
     *
     * @param data
     * @return
     */
    public Result setData(Object data) {
        if (data instanceof Record){
            Record d = (Record) data;
            this.put("data", d.getColumns());
        }
        if (data instanceof ArrayList){
            ArrayList arr = (ArrayList) data;
            for (Object o : arr) {
                if (o instanceof Record){
                    Record o1 = (Record) o;
                    o = o1.getColumns();
                }
            }
        }
        this.put("data", data);
        return this;
    }


    public <T> T getData(){
        return (T)this.get("data");
    }

    /**
     * 执行结果
     *
     * @return
     */
    public boolean isSuccess() {
        return (Boolean) this.get("success");
    }

    /**
     * 执行返回状态码
     *
     * @param state
     * @return
     */
    public Result setState(int state) {
        this.put("state", state);
        return this;
    }

    public Result success(Object data) {
        return this.setSuccess(true)
                .setMessage("成功")
                .setState(SUCCESS)
                .setData(data == null ? EMPTY : data);
    }

    public Result success() {
        return success(null);
    }


    public Result error(Object data) {
        return this.setSuccess(false)
                .setMessage("失败")
                .setState(ERROR)
                .setData(data == null ? EMPTY : data);
    }

    public Result error() {
        return error(null);
    }

    @Override
    public Result put(String key, Object value) {
        // TODO Auto-generated method stub
        super.put(key, value);
        return this;
    }


}
