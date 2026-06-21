package com.hms.backend.config;

import com.hms.backend.entity.Role;
import com.hms.backend.entity.User;
import com.hms.backend.entity.UserRole;
import com.hms.backend.repository.RoleRepository;
import com.hms.backend.repository.UserRepository;
import com.hms.backend.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        Role adminRole = createRoleIfNotExists(
                "ADMIN",
                "Admin",
                "System administrator with full access"
        );

        Role doctorRole = createRoleIfNotExists(
                "DOCTOR",
                "Doctor",
                "Doctor can view appointments and prescriptions"
        );

        Role nurseRole = createRoleIfNotExists(
                "NURSE",
                "Nurse",
                "Nurse can manage patient care details"
        );

        Role pharmacistRole = createRoleIfNotExists(
                "PHARMACIST",
                "Pharmacist",
                "Pharmacist can manage medicines and inventory"
        );

        Role patientRole = createRoleIfNotExists(
                "PATIENT",
                "Patient",
                "Patient can view own appointments and records"
        );

        Role receptionistRole = createRoleIfNotExists(
                "RECEPTIONIST",
                "Receptionist",
                "Receptionist can register patients and schedule appointments"
        );

        createUserIfNotExists(
                "admin1",
                "admin123",
                "System Admin",
                "admin@hms.com",
                "9999999991",
                adminRole
        );

        createUserIfNotExists(
                "doctor1",
                "doctor123",
                "Dr. Amit Sharma",
                "doctor@hms.com",
                "9999999992",
                doctorRole
        );

        createUserIfNotExists(
                "nurse1",
                "nurse123",
                "Nurse Priya",
                "nurse@hms.com",
                "9999999993",
                nurseRole
        );

        createUserIfNotExists(
                "pharma1",
                "pharma123",
                "Pharmacist Rahul",
                "pharma@hms.com",
                "9999999994",
                pharmacistRole
        );

        createUserIfNotExists(
                "patient1",
                "patient123",
                "Patient Ramesh",
                "patient@hms.com",
                "9999999995",
                patientRole
        );

        createUserIfNotExists(
                "reception1",
                "reception123",
                "Receptionist Neha",
                "reception@hms.com",
                "9999999996",
                receptionistRole
        );
    }

    private Role createRoleIfNotExists(String roleCode, String roleName, String description) {

        return roleRepository.findByRoleCode(roleCode)
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .roleCode(roleCode)
                            .roleName(roleName)
                            .description(description)
                            .build();

                    return roleRepository.save(role);
                });
    }

    private void createUserIfNotExists(
            String username,
            String rawPassword,
            String fullName,
            String email,
            String phone,
            Role role
    ) {

        User user = userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .username(username)
                            .passwordHash(passwordEncoder.encode(rawPassword))
                            .fullName(fullName)
                            .email(email)
                            .phone(phone)
                            .active(true)
                            .accountLocked(false)
                            .build();

                    return userRepository.save(newUser);
                });

        boolean mappingExists = userRoleRepository
                .existsByUserUserIdAndRoleRoleId(user.getUserId(), role.getRoleId());

        if (!mappingExists) {
            UserRole userRole = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();

            userRoleRepository.save(userRole);
        }
    }
}