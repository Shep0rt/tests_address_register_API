package org.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

// Централизованная конфигурация API-тестов.
// Приоритет источников: -D параметры JVM -> переменные окружения -> application.properties -> дефолт.
public record AppConfig(
        String baseUrl,
        int connectTimeoutMs,
        int readTimeoutMs,
        String apiKey
) {
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 5000;
    private static final int DEFAULT_READ_TIMEOUT_MS = 10000;

    public static AppConfig load() {
        Properties properties = new Properties();

        try (InputStream inputStream = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream != null) {
                // Явно читаем properties как UTF-8, чтобы поддерживать кириллицу.
                properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать файл application.properties", e);
        }

        String baseUrl = resolve("api.base-url", "API_BASE_URL", properties, DEFAULT_BASE_URL);
        int connectTimeout = resolveInt(
                "api.connect-timeout-ms",
                "API_CONNECT_TIMEOUT_MS",
                properties,
                DEFAULT_CONNECT_TIMEOUT_MS
        );
        int readTimeout = resolveInt(
                "api.read-timeout-ms",
                "API_READ_TIMEOUT_MS",
                properties,
                DEFAULT_READ_TIMEOUT_MS
        );
        String apiKey = resolve("api.x-api-key", "API_X_API_KEY", properties, "");

        return new AppConfig(baseUrl, connectTimeout, readTimeout, apiKey);
    }

    private static String resolve(String propertyKey, String envKey, Properties properties, String defaultValue) {
        String systemValue = System.getProperty(propertyKey);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        String propertyValue = properties.getProperty(propertyKey);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        return defaultValue;
    }

    private static int resolveInt(String propertyKey, String envKey, Properties properties, int defaultValue) {
        String value = resolve(propertyKey, envKey, properties, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Некорректное целочисленное значение для %s: %s".formatted(propertyKey, value),
                    e
            );
        }
    }
}