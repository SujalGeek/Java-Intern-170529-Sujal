package com.fitness.userservice.repository;

import com.fitness.userservice.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

    boolean existsByEmail(String email);

    Boolean exitsByKeyCloakId(String userId);

    User findByEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid Email format") String email);
}
