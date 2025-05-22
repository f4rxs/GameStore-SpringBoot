package com.example.gamestoreclient.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    // Generic GET request
    public static <T> T get(String url, Class<T> responseType) throws IOException {
        HttpURLConnection connection = createGetConnection(url);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return objectMapper.readValue(response.toString(), responseType);
        } finally {
            connection.disconnect();
        }
    }

    // GET request for lists
    public static <T> List<T> getList(String url, TypeReference<List<T>> typeReference) throws IOException {
        HttpURLConnection connection = createGetConnection(url);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return objectMapper.readValue(response.toString(), typeReference);
        } finally {
            connection.disconnect();
        }
    }

    // POST request
    public static <T> T post(String url, Object requestBody, Class<T> responseType) throws IOException {
        HttpURLConnection connection = createConnection(url, "POST");

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return objectMapper.readValue(response.toString(), responseType);
        } finally {
            connection.disconnect();
        }
    }

    // PUT request
    public static <T> T put(String url, Object requestBody, Class<T> responseType) throws IOException {
        HttpURLConnection connection = createConnection(url, "PUT");

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return objectMapper.readValue(response.toString(), responseType);
        } finally {
            connection.disconnect();
        }
    }

    // DELETE request
    public static void delete(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            throw new IOException("HTTP Error: " + responseCode);
        }

        connection.disconnect();
    }

    private static HttpURLConnection createGetConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            throw new IOException("HTTP Error: " + responseCode);
        }

        return connection;
    }

    private static HttpURLConnection createConnection(String url, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        return connection;
    }
}
