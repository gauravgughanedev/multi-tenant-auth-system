package dev.gauravgughane.code.auth.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

public class TenantFilter {

    @Slf4j
    @Component
    @Order(1)
    public class TenantFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String tenantId = httpRequest.getHeader("X-Tenant-ID");
            String projectId = httpRequest.getHeader("X-Project-ID");

            // Use project ID if available, otherwise fall back to tenant ID
            String finalTenantId = projectId != null ? projectId : tenantId;

            if (finalTenantId == null || finalTenantId.isEmpty()) {
                finalTenantId = "default";
            }

            log.debug("Setting tenant context to: {}", finalTenantId);
            TenantContext.setTenantId(finalTenantId);

            try {
                chain.doFilter(request, response);
            } finally {
                TenantContext.clear();
            }
        }
}
