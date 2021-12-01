package com.github.sparkzxl.database.support;

import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.support.TenantException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.core.Ordered;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

/**
 * description: 数据库全局异常处理
 *
 * @author zhoux
 */
@RestControllerAdvice
@RestController
@Slf4j
@ResponseResultStatus
public class DataBaseExceptionHandler implements Ordered {

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ResponseResult<?> handleSqlSyntaxErrorException(SQLSyntaxErrorException e) {
        log.error("SQL异常：", e);
        return ResponseResult.result(ResponseInfoStatus.SQL_EX);
    }

    @ExceptionHandler(TooManyResultsException.class)
    public ResponseResult<?> handleTooManyResultsException(TooManyResultsException e) {
        log.error("查询异常：", e);
        return ResponseResult.result(ResponseInfoStatus.SQL_MANY_RESULT_EX);
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseResult<?> handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error("SQL异常：", e);
        return ResponseResult.result(ResponseInfoStatus.FAILURE.getCode(), e.getMessage());
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseResult<?> persistenceException(PersistenceException e) {
        log.error("数据库异常：", e);
        if (e.getCause() instanceof BizException) {
            BizException cause = (BizException) e.getCause();
            return ResponseResult.result(cause.getCode(), cause.getMessage());
        }
        return ResponseResult.result(ResponseInfoStatus.SQL_EX.getCode(), e.getMessage());
    }

    @ExceptionHandler(MyBatisSystemException.class)
    public ResponseResult<?> myBatisSystemException(MyBatisSystemException e) {
        log.error("Mybatis异常：", e);
        if (e.getCause() instanceof PersistenceException) {
            return this.persistenceException((PersistenceException) e.getCause());
        }
        return ResponseResult.result(ResponseInfoStatus.SQL_EX.getCode(), ResponseInfoStatus.SQL_EX.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public ResponseResult<?> sqlException(SQLException e) {
        log.error("SQL异常：", e);
        return ResponseResult.result(ResponseInfoStatus.SQL_EX.getCode(), e.getMessage());
    }

    @ExceptionHandler(TenantException.class)
    public ResponseResult<?> handleTenantException(TenantException e) {
        log.error("租户异常：", e);
        return ResponseResult.result(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseResult<?> handler(DuplicateKeyException e) {
        log.error("数据重复输入: ", e);
        return ResponseResult.result(ResponseInfoStatus.SQL_EX.getCode(), "数据重复输入");
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseResult<?> handler(DataIntegrityViolationException e) {
        log.error("数据库操作异常:", e);
        String message = e.getMessage();
        String prefix = "Data too long";
        if (message.contains(prefix)) {
            return ResponseResult.result(ResponseInfoStatus.SQL_EX.getCode(), "输入数据字段过长");
        }
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            SQLException sqlException = (SQLException) cause;
            int errorCode = sqlException.getErrorCode();
            if (errorCode == 1364) {
                return ResponseResult.result(ResponseInfoStatus.SQL_EX.getCode(), "数据操作异常,输入参数为空");
            }
        }
        return ResponseResult.result(ResponseInfoStatus.SQL_EX);
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.DATASOURCE_EXCEPTION_HANDLER_ORDER.getOrder();
    }
}