package com.github.sparkzxl.core.assert_;

import com.github.sparkzxl.core.base.code.BaseEnumCode;
import com.github.sparkzxl.core.support.BaseException;
import com.github.sparkzxl.core.support.BizException;

import java.text.MessageFormat;

/**
 * description:
 *
 * @author zhouxinlei
 */
public interface BusinessEnumSysAssert extends BaseEnumCode, BusinessAssert {

    /**
     * 创建异常
     *
     * @param args 入参
     * @return BaseException
     */
    @Override
    default BaseException newException(Object... args) {
        String msg = MessageFormat.format(this.getMessage(), args);
        return new BizException(this, args, msg);
    }

    /**
     * 创建异常
     *
     * @param t    t
     * @param args 入参
     * @return BaseException
     */
    @Override
    default BaseException newException(Throwable t, Object... args) {
        String msg = MessageFormat.format(this.getMessage(), args);
        return new BizException(this, args, msg, t);
    }
}
