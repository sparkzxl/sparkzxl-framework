package com.github.sparkzxl.feign.decoder;

import cn.hutool.core.bean.OptionalBean;
import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.entity.response.Response;
import com.github.sparkzxl.feign.exception.RemoteCallException;
import feign.Request;
import feign.RequestTemplate;
import feign.Target;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * description: 当调用远程服务 其抛出异常捕获
 *
 * @author zhouxinlei
 */
@Slf4j
public class FeignExceptionDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, feign.Response response) {
        String applicationName =
                OptionalBean.ofNullable(response.request()).getBean(Request::requestTemplate).getBean(RequestTemplate::feignTarget).getBean(Target::name)
                        .orElseGet(() -> "unKnownServer");
        try {
            Reader reader = response.body().asReader(StandardCharsets.UTF_8);
            String body = Util.toString(reader);
            Response<?> responseResult = JsonUtil.parse(body, Response.class);
            return new RemoteCallException(responseResult.getErrorCode(), responseResult.getErrorMsg(), applicationName);
        } catch (Exception e) {
            log.error("[{}] has an unknown exception.", methodKey, e);
            return new RemoteCallException(ExceptionErrorCode.FAILURE.getErrorCode(), "unKnowException", applicationName, e);
        }

    }
}