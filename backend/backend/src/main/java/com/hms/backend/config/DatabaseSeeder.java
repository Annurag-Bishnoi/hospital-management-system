package com.hms.backend.config;

import com.hms.backend.entity.MedicalConcept;
import com.hms.backend.repository.MedicalConceptRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(MedicalConceptRepository repository) {
        return args -> {
            boolean hasDiagnosis = repository.findAll().stream().anyMatch(c -> "Diagnosis".equals(c.getConceptClass()));
            // Check if database is already fully seeded so we don't duplicate data on restart
            if (repository.count() == 0 || !hasDiagnosis) {
                System.out.println("Seeding CIEL Dictionary from JSON (Vitals, Drugs, and Diagnoses)...");
                repository.deleteAll(); // clear partial seeds

                ObjectMapper mapper = new ObjectMapper();
                ClassPathResource resource = new ClassPathResource("ciel_dictionary.json");

                try {
                    // Parse the JSON file
                    JsonNode rootNode = mapper.readTree(resource.getInputStream());

                    // TARGET THE CONCEPTS ARRAY SPECIFICALLY
                    JsonNode conceptsArray = rootNode.path("concepts");

                    // Fallback just in case OCL changes their export format to a direct array
                    if (conceptsArray.isMissingNode() && rootNode.isArray()) {
                        conceptsArray = rootNode;
                    }

                    List<MedicalConcept> conceptsToSave = new ArrayList<>();

                    // Loop through the specific concepts array
                    for (JsonNode node : conceptsArray) {
                        String conceptClass = node.path("concept_class").asText();

                        // We want to save Vitals ("Test"), Medicines ("Drug"), Diagnoses ("Diagnosis", "Symptom", "Finding")
                        if ("Test".equals(conceptClass) || "Drug".equals(conceptClass) || 
                            "Diagnosis".equals(conceptClass) || "Symptom".equals(conceptClass) || 
                            "Finding".equals(conceptClass)) {

                            String id = node.path("id").asText();

                            // Grab the primary English name
                            String name = "Unknown";
                            JsonNode namesNode = node.path("names");
                            if (namesNode.isArray() && !namesNode.isEmpty()) {
                                name = namesNode.get(0).path("name").asText();
                            }

                            conceptsToSave.add(new MedicalConcept(id, name, conceptClass));
                        }
                    }

                    // Save everything to your database
                    repository.saveAll(conceptsToSave);
                    System.out.println("Successfully seeded " + conceptsToSave.size() + " CIEL concepts into the database!");

                } catch (Exception e) {
                    System.err.println("Error reading the JSON file: " + e.getMessage());
                }
            } else {
                System.out.println("CIEL Concepts (including Diagnosis) are already loaded in the database.");
            }
        };
    }
}
