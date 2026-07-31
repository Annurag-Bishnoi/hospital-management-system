package com.hms.backend.config;

import com.hms.backend.entity.MedicalConcept;
import com.hms.backend.repository.MedicalConceptRepository;
import com.hms.backend.entity.User;
import com.hms.backend.entity.Role;
import com.hms.backend.entity.UserRole;
import com.hms.backend.repository.UserRepository;
import com.hms.backend.repository.RoleRepository;
import com.hms.backend.repository.UserRoleRepository;
import com.hms.backend.ipd.repository.WardRepository;
import com.hms.backend.ipd.repository.BedRepository;
import com.hms.backend.ipd.entity.Ward;
import com.hms.backend.ipd.entity.Bed;
import com.hms.backend.patients.entity.InsuranceProvider;
import com.hms.backend.patients.repository.InsuranceProviderRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(
            MedicalConceptRepository repository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            WardRepository wardRepository,
            BedRepository bedRepository,
            InsuranceProviderRepository insuranceRepo,
            PasswordEncoder passwordEncoder) {
        return args -> {
            boolean hasDiagnosis = false;
            try {
                hasDiagnosis = repository.existsByConceptClass("Diagnosis");
            } catch (Exception e) {
                // Ignore if table not created yet or other issues
            }
            
            // Check if database is already fully seeded so we don't duplicate data on restart
            if (repository.count() == 0 || !hasDiagnosis) {
                System.out.println("Seeding CIEL Dictionary from JSON (Vitals, Drugs, and Diagnoses)...");
                repository.deleteAll(); // clear partial seeds

                ObjectMapper mapper = new ObjectMapper();
                ClassPathResource resource = new ClassPathResource("ciel_dictionary.json");

                try {
                    // Use Jackson Streaming API for low memory footprint in deployment environments
                    try (tools.jackson.core.JsonParser parser = mapper.createParser(resource.getInputStream())) {
                        List<MedicalConcept> conceptsToSave = new ArrayList<>();
                        while (!parser.isClosed()) {
                            tools.jackson.core.JsonToken token = parser.nextToken();
                            if (token == null) break;
                            
                            if (token == tools.jackson.core.JsonToken.START_OBJECT) {
                                // Parse this specific object into a JsonNode tree
                                JsonNode node = mapper.readTree(parser);
                                
                                // OCL exports stream of objects. Concepts have type='Concept'
                                String type = node.path("type").asText();
                                if ("Concept".equals(type) || node.has("concept_class")) {
                                    String conceptClass = node.path("concept_class").asText();
                                    if ("Test".equals(conceptClass) || "Drug".equals(conceptClass) || 
                                        "Diagnosis".equals(conceptClass) || "Symptom".equals(conceptClass) || 
                                        "Finding".equals(conceptClass)) {

                                        String id = node.path("id").asText();
                                        String name = "Unknown";
                                        JsonNode namesNode = node.path("names");
                                        if (namesNode.isArray() && !namesNode.isEmpty()) {
                                            name = namesNode.get(0).path("name").asText();
                                        }

                                        conceptsToSave.add(new MedicalConcept(id, name, conceptClass));
                                    }
                                }
                                
                                // Save in batches to prevent memory spikes
                                if (conceptsToSave.size() >= 1000) {
                                    repository.saveAll(conceptsToSave);
                                    conceptsToSave.clear();
                                }
                            }
                        }
                        
                        // Save any remaining concepts
                        if (!conceptsToSave.isEmpty()) {
                            repository.saveAll(conceptsToSave);
                        }
                    }

                    System.out.println("=== ROLES ===");
                    roleRepository.findAll().forEach(r -> System.out.println(r.getRoleName()));

                } catch (Exception e) {
                    System.err.println("Error reading the JSON file: " + e.getMessage());
                }
            } else {
                System.out.println("CIEL Concepts (including Diagnosis) are already loaded in the database.");
            }

            String[][] rolesAndUsers = {
                {"ADMIN", "Administrator", "admin01", "Super Admin", "admin@hospital.com", "9990000001"},
                {"DOCTOR", "Doctor", "doctor01", "Dr. John Doe", "doctor@hospital.com", "9990000002"},
                {"RECEPTIONIST", "Receptionist", "reception01", "Front Desk", "reception@hospital.com", "9990000003"},
                {"PHARMACIST", "Pharmacist", "pharma01", "Pharmacy Lead", "pharma@hospital.com", "9990000004"},
                {"BILLING", "Billing Staff", "billing01", "Billing Dept", "billing@hospital.com", "9990000005"},
                {"LABORATORY", "Lab Technician", "lab01", "Lab Tech", "lab@hospital.com", "9990000006"},
                {"PATIENT", "Patient", "patient01", "Test Patient", "patient@hospital.com", "9990000007"},
                {"NURSE", "Nurse", "nurse01", "Nurse Jane", "nurse@hospital.com", "9990000008"}
            };

            for (String[] data : rolesAndUsers) {
                String roleCode = data[0];
                String roleName = data[1];
                String username = data[2];
                String fullName = data[3];
                String email = data[4];
                String phone = data[5];

                Role role = roleRepository.findByRoleCode(roleCode)
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setRoleCode(roleCode);
                        r.setRoleName(roleName);
                        return roleRepository.save(r);
                    });

                if (userRepository.findByUsername(username).isEmpty()) {
                    System.out.println("Seeding initial " + roleCode + " user: " + username);
                    User user = new User();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPasswordHash(passwordEncoder.encode("Anurag@123"));
                    user.setFullName(fullName);
                    user.setPhone(phone); // ensure unique phone
                    user.setActive(true);
                    userRepository.save(user);

                    UserRole ur = new UserRole();
                    ur.setUser(user);
                    ur.setRole(role);
                    userRoleRepository.save(ur);
                }
            }
            System.out.println("Default users seeded successfully.");
            
            if (wardRepository.count() == 0) {
                System.out.println("Seeding IPD Wards and Beds...");
                Ward gw = wardRepository.save(Ward.builder().name("General Ward").capacity(10).dailyCharge(BigDecimal.valueOf(1000.0)).build());
                Ward icu = wardRepository.save(Ward.builder().name("Intensive Care Unit (ICU)").capacity(5).dailyCharge(BigDecimal.valueOf(5000.0)).build());
                Ward vip = wardRepository.save(Ward.builder().name("VIP Suite").capacity(2).dailyCharge(BigDecimal.valueOf(10000.0)).build());

                for (int i = 1; i <= 10; i++) bedRepository.save(Bed.builder().bedNumber("GW-" + i).ward(gw).status("AVAILABLE").build());
                for (int i = 1; i <= 5; i++) bedRepository.save(Bed.builder().bedNumber("ICU-" + i).ward(icu).status("AVAILABLE").build());
                for (int i = 1; i <= 2; i++) bedRepository.save(Bed.builder().bedNumber("VIP-" + i).ward(vip).status("AVAILABLE").build());
            }

            if (insuranceRepo.count() == 0) {
                System.out.println("Seeding Insurance Providers...");
                insuranceRepo.save(InsuranceProvider.builder().providerName("HDFC Ergo Health").standardCoveragePercentage(80.0).build());
                insuranceRepo.save(InsuranceProvider.builder().providerName("Star Health Insurance").standardCoveragePercentage(100.0).build());
                insuranceRepo.save(InsuranceProvider.builder().providerName("ICICI Lombard").standardCoveragePercentage(90.0).build());
            }
            
        };
    }
}
