package com.zo.platform.starter.exception.advice;

import com.zo.platform.sdk.common.Result;
import com.zo.platform.starter.exception.ZoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. 自定义业务异常处理
     * 业务主动抛出的预期异常，打印警告日志，返回业务提示
     */
    @ExceptionHandler(ZoException.class)
    public Result handleBusinessException(ZoException e, HttpServletRequest request) {
        log.warn("业务异常 | 请求地址：{} | 错误信息：{}", request.getRequestURI(), e.getMessage());
        return Result.me().error().setMessage(e.getMessage());
    }


    /**
     * 5. 缺少必填请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result handleMissingParamException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        String msg = "缺少必填参数：" + e.getParameterName();
        log.warn("缺少必填参数 | 请求地址：{} | {}", request.getRequestURI(), msg);
        return Result.me().error().setMessage(msg);
    }

    /**
     * 6. 参数类型转换失败（如数字传了字符串）
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result handleTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String msg = String.format("参数[%s]类型错误，期望类型：%s",
                e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知");
        log.warn("参数类型错误 | 请求地址：{} | {}", request.getRequestURI(), msg);
        return Result.me().error().setMessage(msg);
    }

    /**
     * 7. 请求体解析失败（JSON格式错误）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result handleMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("请求体解析失败 | 请求地址：{} | 错误信息：{}", request.getRequestURI(), e.getMessage());
        return Result.me().error().setMessage("请求体格式错误，请检查JSON格式");
    }


    /**
     * 8. 404 资源不存在
     * 需配合配置：spring.mvc.throw-exception-if-no-handler-found=true
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("请求资源不存在 | {} {}", e.getHttpMethod(), e.getRequestURL());
        return Result.me().error().setMessage("未找到页面或请求");
    }

    /**
     * 9. 请求方法不支持（POST接口用GET请求）
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("请求方法不支持 | 请求地址：{} | 当前方法：{}",
                request.getRequestURI(), e.getMethod());
        return Result.me().error().setMessage("不支持" + e.getMethod() + "请求方式");
    }

    /**
     * 10. 请求媒体类型不支持（Content-Type错误）
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result handleMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        log.warn("媒体类型不支持 | 请求地址：{} | 当前类型：{}",
                request.getRequestURI(), e.getContentType());
        return Result.me().error().setMessage("媒体类型不支持");
    }

    /**
     * 11. 文件上传大小超限
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException e, HttpServletRequest request) {
        log.warn("文件上传超限 | 请求地址：{}", request.getRequestURI());
        return Result.me().error().setMessage("文件上传超出大小限制");
    }

    /**
     * 12. 兜底异常处理
     * 捕获所有未预期的系统异常，不暴露内部错误信息，打印完整错误堆栈
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常 | 请求地址：{} | 请求方式：{}",
                request.getRequestURI(), request.getMethod(), e);
        return Result.me().error().setMessage("系统异常, 请联系管理人员");
    }
}