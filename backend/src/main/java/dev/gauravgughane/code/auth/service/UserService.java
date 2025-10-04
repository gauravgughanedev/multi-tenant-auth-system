package dev.gauravgughane.code.auth.service;

import dev.gauravgughane.code.auth.entity.BaseUser;
import dev.gauravgughane.code.auth.repository.UserRepository;
import dev.gauravgughane.code.auth.interceptor.TenantInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<BaseUser> findAllUsers() {
        String currentTenant = TenantInterceptor.getCurrentTenant();
        System.out.println("Current tenant in UserService: " + currentTenant);

        if (currentTenant == null) {
            throw new RuntimeException("No tenant context available");
        }

        try {

            List<BaseUser> users = userRepository.findAll();
            System.out.println("Found " + users.size() + " users for tenant: " + currentTenant);

            users.forEach(user -> {
                System.out.println("User: " + user.getEmail() + " (ID: " + user.getId() + ")");
            });

            return users;
        } catch (Exception e) {
            System.err.println("Error in findAllUsers for tenant " + currentTenant + ": " + e.getMessage());
            throw e;
        }
    }
}