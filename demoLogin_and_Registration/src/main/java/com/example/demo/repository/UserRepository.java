package com.example.demo.repository;

import com.example.demo.entity.AuthenticationProvider;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);;

    public User findByResetPasswordToken(String token);

    User findByName(String username);

    @Modifying
    @Query("UPDATE User u SET u.provider = ?2 WHERE u.email = ?1")
    public void updateAuthenticationProvider(String email, AuthenticationProvider authType);
}
