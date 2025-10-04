package dev.gauravgughane.code.auth.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId != null && !tenantId.trim().isEmpty()) {
            // Normalize tenant ID for schema naming
            if (!tenantId.equals("public") && !tenantId.startsWith("tenant_")) {
                return "tenant_" + tenantId.toLowerCase();
            }
            return tenantId.toLowerCase();
        }
        return "public";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}