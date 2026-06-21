package com.hms.backend.service;

import com.hms.backend.dto.LoginRequest;
import com.hms.backend.dto.LoginResponse;
import com.hms.backend.entity.User;
import com.hms.backend.entity.UserRole;
import com.hms.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new RuntimeException("Account is inactive. Please contact admin.");
        }

        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            throw new RuntimeException("Account is locked. Please contact admin.");
        }

        boolean passwordMatched = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatched) {
            throw new RuntimeException("Invalid username or password");
        }

        Set<UserRole> userRoles = user.getUserRoles();

        if (userRoles == null || userRoles.isEmpty()) {
            throw new RuntimeException("No role assigned to this user");
        }

        String roleCode = userRoles
                .iterator()
                .next()
                .getRole()
                .getRoleCode();

        return LoginResponse.builder()
                .success(true)
                .message("Login successful")
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(roleCode)
                .build();
    }
}