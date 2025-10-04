
package dev.gauravgughane.code.auth.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class TenantFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String tenantId = httpRequest.getHeader("X-Project-ID");
        if (tenantId == null || tenantId.isBlank()) {
            tenantId = httpRequest.getHeader("X-Tenant-ID");
        }
        if (tenantId == null || tenantId.isBlank()) {
            tenantId = "public";
        }

        log.debug("Setting tenant context to: {}", tenantId);
        TenantContext.setTenantId(tenantId);

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}