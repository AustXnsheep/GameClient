package git.austxnsheep.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }
    public static void saveJsonToFile(String json, String filePath) throws IOException {
        mapper.writeValue(new File(filePath), json);
    }
    public static <T> T fromJsonFile(String filePath, Class<T> valueType) throws IOException {
        return mapper.readValue(new File(filePath), valueType);
    }
}
