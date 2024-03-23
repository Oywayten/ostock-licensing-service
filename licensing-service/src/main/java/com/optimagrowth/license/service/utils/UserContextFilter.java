package com.optimagrowth.license.service.utils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class UserContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        UserContext userContext = UserContextHolder.getContext();
        String value;

        value = httpServletRequest.getHeader(UserContext.CORRELATION_ID);
        userContext.setCorrelationId(value);

        value = httpServletRequest.getHeader(UserContext.USER_ID);
        userContext.setUserId(value);

        value = httpServletRequest.getHeader(UserContext.AUTH_TOKEN);
        userContext.setAuthToken(value);

        value = httpServletRequest.getHeader(UserContext.ORGANIZATION_ID);
        userContext.setOrganizationId(value);

        log.debug("UserContextFilter Correlation id: {}", userContext.getCorrelationId());
        filterChain.doFilter(httpServletRequest, servletResponse);
    }
}
