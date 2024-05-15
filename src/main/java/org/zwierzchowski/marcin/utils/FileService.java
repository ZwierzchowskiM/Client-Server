package org.zwierzchowski.marcin.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.zwierzchowski.marcin.user.User;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String FILE_PATH = "./users.json";

    private FileService() {
    }

    public static Map<String, User> loadDataBase() throws IOException {
        File file = new File(FILE_PATH);
        if (file.exists() && file.length() != 0) {
            return OBJECT_MAPPER.readValue(file, new TypeReference<Map<String,User>>() {});
        } else {
            return new HashMap<>();
        }
    }

    public static void saveDataBase(Map<String,User> users) throws IOException {
        File file = new File(FILE_PATH);
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        OBJECT_MAPPER.writerFor(new TypeReference<Map<String,User>>() { }).writeValue(file, users);
    }
}
