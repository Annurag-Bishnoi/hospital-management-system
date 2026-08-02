import java.io.*;
import java.util.zip.*;
import java.nio.file.*;
public class TestZip {
    public static void main(String[] args) throws Exception {
        Path p = Paths.get("backend/backend/src/main/resources/ciel_dictionary.zip");
        if (!Files.exists(p)) { System.out.println("Not found!"); return; }
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(p))) {
            ZipEntry entry = zis.getNextEntry();
            System.out.println("Entry: " + (entry == null ? "null" : entry.getName()));
        }
    }
}
