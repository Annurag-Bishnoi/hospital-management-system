package com.hms.backend;

import com.hms.backend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleDumper implements CommandLineRunner {
    private final RoleRepository roleRepository;
    public RoleDumper(RoleRepository roleRepository) { this.roleRepository = roleRepository; }
    
    @Override
    public void run(String... args) {
        System.out.println("=== ROLES ===");
        roleRepository.findAll().forEach(r -> System.out.println(r.getRoleCode()));
    }
}
