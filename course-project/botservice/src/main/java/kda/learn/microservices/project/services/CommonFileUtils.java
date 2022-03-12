package kda.learn.microservices.project.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CommonFileUtils {
    public static String loadDataFile(String fileName) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = loader.getResourceAsStream(fileName)) {
            if (inputStream == null) throw new IOException("Не удалось загрузить файл с данными " + fileName);
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
