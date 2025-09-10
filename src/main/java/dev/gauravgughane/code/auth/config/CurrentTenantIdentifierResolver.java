package dev.gauravgughane.code.auth.config;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

public class CurrentTenantIdentifierResolver {

    @Component
    public class CurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {

        private static final String DEFAULT_TENANT = "public";

        @Override
        public String resolveCurrentTenantIdentifier() {
            String tenant = TenantContext.getTenantId();
            return tenant != null ? tenant : DEFAULT_TENANT;
        }

        @Override
        public boolean validateExistingCurrentSessions() {
            return true;
        }

        @Override
        public void customize(Map<String, Object> hibernateProperties) {
            hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
        }
    }
}
