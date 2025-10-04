package dev.gauravgughane.code.auth.repository;

import dev.gauravgughane.code.auth.entity.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BaseUserRepository extends JpaRepository<BaseUser, UUID> {
    Optional<BaseUser> findByEmail(String email); // This method is used by UserService
    
}