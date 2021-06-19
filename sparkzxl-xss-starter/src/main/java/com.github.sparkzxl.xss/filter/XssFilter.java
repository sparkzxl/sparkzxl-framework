package com.github.sparkzxl.xss.filter;


import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.core.utils.StringHandlerUtils;
import com.github.sparkzxl.xss.wrapper.XssRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * description: 跨站工具 过滤器
 *
 * @author zhouxinlei
 */
@Slf4j
public class XssFilter implements Filter {

    /**
     * 可放行的请求路径
     */

    public static final String IGNORE_PATH = "ignorePath";
    /**
     * 可放行的参数值
     */
    public static final String IGNORE_PARAM_VALUE = "ignoreParamValue";

    /**
     * 可放行的请求路径列表
     */
    private List<String> ignorePathList;
    /**
     * 可放行的参数值列表
     */
    private List<String> ignoreParamValueList;

    @Override
    public void init(FilterConfig fc) {
        this.ignorePathList = StrUtil.split(fc.getInitParameter(IGNORE_PATH), CharUtil.COMMA);
        this.ignoreParamValueList = StrUtil.split(fc.getInitParameter(IGNORE_PARAM_VALUE), CharUtil.COMMA);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 判断uri是否包含项目名称
        String uriPath = ((HttpServletRequest) request).getRequestURI();
        if (StringHandlerUtils.matchUrl(ignorePathList, uriPath)) {
            log.debug("忽略过滤路径=[{}]", uriPath);
            chain.doFilter(request, response);
            return;
        }
        log.debug("过滤器包装请求路径=[{}]", uriPath);
        chain.doFilter(new XssRequestWrapper((HttpServletRequest) request, ignoreParamValueList), response);
    }
}
