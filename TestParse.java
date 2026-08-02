import java.io.*;
import java.nio.file.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class TestParse {
    public static void main(String[] args) throws Exception {
        Path p = Paths.get("backend/backend/src/main/resources/ciel_dictionary.json");
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = Files.newInputStream(p);
             JsonParser parser = mapper.createParser(is)) {
            int count = 0;
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == null) break;
                if (token == JsonToken.START_OBJECT) {
                    JsonNode node = mapper.readTree(parser);
                    String type = node.path("type").asText();
                    if ("Concept".equals(type)) {
                        String conceptClass = node.path("concept_class").asText();
                        if ("Test".equals(conceptClass) || "Drug".equals(conceptClass) || "Diagnosis".equals(conceptClass)) {
                            count++;
                        }
                    }
                    if (count > 0 && count % 5000 == 0) {
                        System.out.println("Parsed " + count + " relevant concepts");
                    }
                }
            }
            System.out.println("Total relevant parsed: " + count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
