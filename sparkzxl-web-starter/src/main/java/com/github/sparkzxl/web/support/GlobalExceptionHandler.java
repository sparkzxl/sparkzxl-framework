package com.github.sparkzxl.web.support;

import com.github.sparkzxl.core.constant.CoreConstant;
import com.github.sparkzxl.core.support.ResponseResultStatus;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.support.BusinessException;
import com.github.sparkzxl.core.support.ServiceDegradeException;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import com.github.sparkzxl.web.annotation.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.openssl.PasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * description: 全局异常处理
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:44:48
 */
@ControllerAdvice
@RestController
@Slf4j
public class GlobalExceptionHandler {

    public void handleResponseResult() {
        HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
        ResponseResult responseResult = (ResponseResult) servletRequest.getAttribute(CoreConstant.RESPONSE_RESULT_ANN);
        boolean result = responseResult != null;
        if (result) {
            servletRequest.removeAttribute(CoreConstant.RESPONSE_RESULT_ANN);
        }
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResult businessException(BusinessException e) {
        handleResponseResult();
        log.error(e.getMessage());
        int code = e.getBaseEnumCode().getCode();
        String message = e.getMessage() == null ? e.getBaseEnumCode().getMessage() : e.getMessage();
        return ApiResult.apiResult(code, message);
    }

    @ExceptionHandler(ServiceDegradeException.class)
    public ApiResult serviceDegradeException(ServiceDegradeException e) {
        handleResponseResult();
        log.error(e.getMessage());
        int code = e.getBaseEnumCode().getCode();
        String message = e.getMessage() == null ? e.getBaseEnumCode().getMessage() : e.getMessage();
        return ApiResult.apiResult(code, message);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult methodArgumentNotValidException(MethodArgumentNotValidException e) {
        handleResponseResult();
        log.error(e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.PARAM_BIND_ERROR.getCode(), bindingResult(e.getBindingResult()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult illegalArgumentException(IllegalArgumentException e) {
        handleResponseResult();
        log.error(e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.PARAM_TYPE_ERROR);
    }

    private String bindingResult(BindingResult bindingResult) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ObjectError objectError : bindingResult.getAllErrors()) {
            stringBuilder.append(", ");
            if (objectError instanceof FieldError) {
                stringBuilder.append(((FieldError) objectError).getField()).append(": ");
            }
            stringBuilder.append(objectError.getDefaultMessage() == null ? "" : objectError.getDefaultMessage());
        }
        return stringBuilder.substring(2);
    }

    @ExceptionHandler({AccountNotFoundException.class, PasswordException.class})
    public ApiResult passwordException(Exception e) {
        handleResponseResult();
        log.error(e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.UN_AUTHORIZED.getCode(), e.getMessage());
    }

    /**
     * 405
     *
     * @param
     * @return ApiResult
     * @author zhouxinlei
     * @date 2019/5/25 0025
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResult httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        handleResponseResult();
        log.error(e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.METHOD_NOT_SUPPORTED);
    }

    /**
     * 404 没有找到访问资源
     *
     * @param
     * @return ApiResult
     * @author zhouxinlei
     * @date 2019/5/25 0025
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResult notFoundPage404(NoHandlerFoundException e) {
        log.error(e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.NOT_FOUND);
    }

    /**
     * 415 不支持媒体类型
     *
     * @param e 异常
     * @return ApiResult
     * @author zhouxinlei
     * @date 2019/5/25 0025
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ApiResult httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error(e.getMessage());
        return ApiResult.apiResult(ResponseResultStatus.MEDIA_TYPE_NOT_SUPPORTED);
    }

    /**
     * 数据库异常
     *
     * @param e 数据库异常
     * @return ApiResult
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public ApiResult handleSQLException(SQLException e) {
        log.error("数据库异常{}", e.getMessage());
        handleResponseResult();
        return ApiResult.apiResult(ResponseResultStatus.SQL_EXCEPTION_ERROR);
    }

}
