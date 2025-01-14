package com.github.sparkzxl.gateway.plugin.jwt;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import com.github.sparkzxl.core.constant.BaseContextConstants;
import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.core.support.JwtExpireException;
import com.github.sparkzxl.core.support.JwtInvalidException;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
import com.github.sparkzxl.core.util.DateUtils;
import com.github.sparkzxl.core.util.SecretUtil;
import com.github.sparkzxl.gateway.common.constant.GatewayConstant;
import com.github.sparkzxl.gateway.common.constant.enums.FilterEnum;
import com.github.sparkzxl.gateway.common.entity.FilterData;
import com.github.sparkzxl.gateway.plugin.core.filter.AbstractGlobalFilter;
import com.github.sparkzxl.gateway.plugin.jwt.handle.JwtRuleHandle;
import com.github.sparkzxl.gateway.utils.ReactorHttpHelper;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * description: jwt filter
 *
 * @author zhouxinlei
 * @since 2022-01-08 23:35:22
 */
@Slf4j
public class JwtFilter extends AbstractGlobalFilter {

    @Override
    public String named() {
        return FilterEnum.JWT.getName();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        FilterData filterData = loadFilterData();
        boolean needSkip = (boolean) exchange.getAttributes().get(GatewayConstant.NEED_SKIP);
        String jsonConfig;
        if (StringUtils.isEmpty(filterData.getConfig())) {
            jsonConfig = "{\"secretKey\":\"\",\"tokenKey\":\"Authorization\"}";
        } else {
            jsonConfig = filterData.getConfig();
        }
        JwtConfig jwtConfig = JsonUtils.getJson().toJavaObject(jsonConfig, JwtConfig.class);
        String ruleHandle;
        if (ObjectUtils.isEmpty(filterData.getRule())) {
            ruleHandle = "{\"converter\":[{\"headerVal\":\"userid\",\"jwtVal\":\"id\"},{\"headerVal\":\"account\",\"jwtVal\":\"username\"},{\"headerVal\":\"name\",\"jwtVal\":\"name\"}]}";
        } else {
            ruleHandle = filterData.getRule().getHandle();
        }
        JwtRuleHandle jwtRuleHandle = JsonUtils.getJson().toJavaObject(ruleHandle, JwtRuleHandle.class);
        assert jwtConfig != null;
        if (needSkip) {
            return removeAuthorization(exchange, chain, jwtConfig.getTokenKey());
        }
        String token = exchange.getRequest().getHeaders().getFirst(jwtConfig.getTokenKey());
        String authToken = StringUtils.removeStartIgnoreCase(token, BaseContextConstants.BEARER_TOKEN);
        Map<String, Object> jsonMap = checkAuthorization(authToken, jwtConfig.getSecretKey());
        if (ObjectUtils.isNotEmpty(jsonMap)) {
            if (ObjectUtils.isNotEmpty(ruleHandle)) {
                return chain.filter(converter(exchange, jsonMap, jwtRuleHandle.getConverter()));
            }
        }
        return ReactorHttpHelper.error(exchange.getResponse(), ResultErrorCode.USER_IDENTITY_VERIFICATION_ERROR);
    }

    @Override
    public int getOrder() {
        return FilterEnum.JWT.getCode();
    }

    /**
     * check Authorization.
     *
     * @param token     token
     * @param secretKey secretKey of authorization
     * @return Map
     */
    private Map<String, Object> checkAuthorization(final String token,
            final String secretKey) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        try {
            JWSObject jwsObject = JWSObject.parse(token);
            if (StringUtils.isNotEmpty(secretKey)) {
                JWSVerifier jwsVerifier = new MACVerifier(SecretUtil.encryptMd5(secretKey));
                if (!jwsObject.verify(jwsVerifier)) {
                    throw new JwtInvalidException("token验签失败");
                }
            }
            Map<String, Object> jsonMap = JsonUtils.getJson().toMap(jwsObject.getPayload().toString());
            long expire = Convert.toLong(jsonMap.get("exp"), 0L);
            DateTime dateTime = DateUtils.date(expire * 1000);
            if (dateTime.getTime() < System.currentTimeMillis()) {
                throw new JwtExpireException("token已过期");
            }
            return jsonMap;
        } catch (Exception e) {
            throw new JwtInvalidException(e);
        }
    }

    /**
     * remove Authorization.
     *
     * @param exchange exchange
     * @param chain    chain
     * @param tokenKey token请求头
     * @return Mono<Void>
     */
    private Mono<Void> removeAuthorization(ServerWebExchange exchange, GatewayFilterChain chain, String tokenKey) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.remove(tokenKey)).build();
        exchange.mutate().request(serverHttpRequest).build();
        return chain.filter(exchange.mutate().request(serverHttpRequest).build());
    }

    /**
     * The parameters in token are converted to request header.
     *
     * @param exchange   exchange
     * @param jsonMap    jsonMap
     * @param converters converters
     * @return ServerWebExchange exchange.
     */
    private ServerWebExchange converter(final ServerWebExchange exchange,
            final Map<String, Object> jsonMap,
            final List<JwtRuleHandle.Convert> converters) {
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .headers(httpHeaders -> this.addHeader(httpHeaders, jsonMap, converters)).build();
        return exchange.mutate().request(modifiedRequest).build();
    }

    /**
     * add header.
     *
     * @param headers    headers
     * @param jsonMap    jsonMap
     * @param converters converters
     */
    private void addHeader(final HttpHeaders headers,
            final Map<String, Object> jsonMap,
            final List<JwtRuleHandle.Convert> converters) {
        for (JwtRuleHandle.Convert converter : converters) {
            headers.add(converter.getHeaderVal(), (String) jsonMap.get(converter.getJwtVal()));
        }
    }
}
