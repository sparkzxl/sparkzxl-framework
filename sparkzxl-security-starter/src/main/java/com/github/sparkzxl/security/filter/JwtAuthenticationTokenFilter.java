package com.github.sparkzxl.security.filter;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.core.base.ResponseResultUtils;
import com.github.sparkzxl.core.entity.JwtUserInfo;
import com.github.sparkzxl.core.support.ResponseResultStatus;
import com.github.sparkzxl.core.support.SparkZxlExceptionAssert;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import com.github.sparkzxl.security.entity.AuthUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * description: jwt认证授权过滤器
 *
 * @author: zhouxinlei
 * @date: 2020-08-07 14:41:50
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private JwtTokenService jwtTokenService;


    public void setJwtTokenService(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String accessToken = ResponseResultUtils.getAuthHeader(request);
        if (StringUtils.isNotEmpty(accessToken)) {
            JwtUserInfo jwtUserInfo = null;
            try {
                jwtUserInfo = jwtTokenService.verifyTokenByHmac(accessToken);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("校验token发生异常：{}", ExceptionUtil.getMessage(e));
                SparkZxlExceptionAssert.businessFail(ResponseResultStatus.JWT_EXPIRED_ERROR);
            }
            String username = jwtUserInfo.getUsername();
            log.info("checking username:{}", username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                AuthUserDetail adminUserDetails = new AuthUserDetail(
                        jwtUserInfo.getId(),
                        jwtUserInfo.getUsername(),
                        null,
                        jwtUserInfo.getName(),
                        true,
                        jwtUserInfo.getAuthorities()
                );
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(adminUserDetails,
                        null, adminUserDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("authenticated user:{}", username);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

}
