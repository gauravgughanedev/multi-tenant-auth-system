package dev.gauravgughane.code.auth.config;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class MultiTenantConnectionProvider {

    @Component
    public class MultiTenantConnectionProvider implements org.hibernate.service.spi.Service {

        @Value("${spring.datasource.url}")
        private String defaultUrl;

        @Value("${spring.datasource.username}")
        private String username;

        @Value("${spring.datasource.password}")
        private String password;

        // Map tenantId -> specific DB URL
        private final Map<String, String> tenantUrls = Map.of(
                "tenant1", "jdbc:postgresql://localhost:5432/tenant1_db",
                "tenant2", "jdbc:postgresql://localhost:5432/tenant2_db"
        );

        @Override
        public Connection getConnection(String tenantIdentifier) throws SQLException {
            String url = tenantUrls.getOrDefault(tenantIdentifier, defaultUrl);
            return DriverManager.getConnection(url, username, password);
        }

        @Override
        public void releaseConnection(Connection connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean supportsAggressiveRelease() {
            return false;
        }

        @Override
        public void stop() {}
    }

}