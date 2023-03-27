package setup;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Parser {

    public static String read(String name) {
        try {
            return Files.readString(Path.of("src", "InputFiles").resolve(name));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static JsonObject readAsJson(String name) {
        return parse(read(name));
    }

    public static JsonObject parse(String content) {
        return (JsonObject) new JsonParser().parse(content);
    }
}
