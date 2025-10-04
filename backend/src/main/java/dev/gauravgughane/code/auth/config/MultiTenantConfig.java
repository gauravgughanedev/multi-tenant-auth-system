package dev.gauravgughane.code.auth.config;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class MultiTenantConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    @Primary
    public AbstractDataSourceBasedMultiTenantConnectionProviderImpl multiTenantConnectionProvider() {
        return new AbstractDataSourceBasedMultiTenantConnectionProviderImpl() {
            @Override
            protected DataSource selectAnyDataSource() {
                return dataSource;
            }

            @Override
            protected DataSource selectDataSource(String tenantIdentifier) {
                return dataSource;
            }
        };
    }
}