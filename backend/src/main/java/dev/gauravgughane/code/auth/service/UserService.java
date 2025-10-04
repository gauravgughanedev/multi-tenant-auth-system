package dev.gauravgughane.code.auth.service;

import dev.gauravgughane.code.auth.entity.BaseUser;
import dev.gauravgughane.code.auth.repository.BaseUserRepository;
import dev.gauravgughane.code.auth.config.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private BaseUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    public BaseUser registerUser(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User with email " + email + " already exists");
        }

        String tenantId = TenantContext.getTenantId();
        if (tenantId != null && !tenantId.equals("public") && !tenantSchemaExists(tenantId)) {
            createTenantSchema(tenantId);
        }

        String hashedPassword = passwordEncoder.encode(password);
        BaseUser user = new BaseUser(name, email, hashedPassword);
        return userRepository.save(user);
    }

    public Optional<BaseUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean checkPassword(BaseUser user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }

    public List<BaseUser> findAll() {
        return userRepository.findAll();
    }

    public List<BaseUser> findAllUsers() {
        return findAll();
    }

    private void createTenantSchema(String tenantId) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String schemaName = "tenant_" + tenantId.toLowerCase();
            stmt.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create tenant schema: " + e.getMessage(), e);
        }
    }

    private boolean tenantSchemaExists(String tenantId) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String schemaName = "tenant_" + tenantId.toLowerCase();
            var rs = stmt.executeQuery(
                "SELECT schema_name FROM information_schema.schemata WHERE schema_name = '" + 
                schemaName + "'"
            );
            return rs.next();
            
        } catch (SQLException e) {
            return false;
        }
    }
}