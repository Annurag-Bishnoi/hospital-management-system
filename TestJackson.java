import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.File;
import java.io.FileInputStream;

public class TestJackson {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("C:/Users/pcc/Downloads/project/HospitalManagementSystem/hospital-management-system/backend/backend/src/main/resources/ciel_dictionary.json");
        try (JsonParser parser = mapper.getFactory().createParser(new FileInputStream(file))) {
            int count = 0;
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == null) break;
                
                if (token == JsonToken.START_OBJECT) {
                    JsonNode node = mapper.readTree(parser);
                    String type = node.path("type").asText();
                    if ("Concept".equals(type) || node.has("concept_class")) {
                        String conceptClass = node.path("concept_class").asText();
                        if ("Test".equals(conceptClass) || "Drug".equals(conceptClass) || 
                            "Diagnosis".equals(conceptClass) || "Symptom".equals(conceptClass) || 
                            "Finding".equals(conceptClass)) {
                            count++;
                        }
                    }
                }
                if (count >= 10) break;
            }
            System.out.println("Found concepts: " + count);
        }
    }
}
