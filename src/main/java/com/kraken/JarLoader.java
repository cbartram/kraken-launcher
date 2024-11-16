package com.kraken;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.runelite.client.plugins.Plugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class JarLoader implements AutoCloseable {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String BOOTSTRAP_URL =
            "https://rog742w0fa.execute-api.us-east-1.amazonaws.com/prod/api/v1/client-bootstrap";

    public JarLoader() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    private String getPresignedUrl() throws Exception {
        HttpRequest bootstrapRequest = HttpRequest.newBuilder()
                .uri(URI.create(BOOTSTRAP_URL))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                bootstrapRequest,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get presigned URL. Status: " +
                    response.statusCode() + ", Body: " + response.body());
        }

        Map<String, Object> responseMap = objectMapper.readValue(
                response.body(),
                Map.class
        );

        String presignedUrl = (String) responseMap.get("URL");

        if (presignedUrl == null || presignedUrl.isEmpty()) {
            throw new RuntimeException("No presigned URL found in response");
        }

        return presignedUrl;
    }


    private byte[] downloadJar() throws Exception {
        String presignedUrl = getPresignedUrl();
        HttpRequest jarRequest = HttpRequest.newBuilder()
                .uri(URI.create(presignedUrl))
                .GET()
                .build();

        HttpResponse<byte[]> jarResponse = httpClient.send(
                jarRequest,
                HttpResponse.BodyHandlers.ofByteArray()
        );

        if (jarResponse.statusCode() != 200) {
            throw new RuntimeException("Failed to download JAR. Status: " +
                    jarResponse.statusCode());
        }

        return jarResponse.body();
    }

    public List<String> listJarClasses(byte[] jarBytes) throws Exception {
        List<String> classNames = new ArrayList<>();

        try (JarInputStream jarStream = new JarInputStream(new java.io.ByteArrayInputStream(jarBytes))) {
            JarEntry entry;
            while ((entry = jarStream.getNextJarEntry()) != null) {
                String name = entry.getName();
                // Only include .class files, exclude package-info.class and module-info.class
                if (name.endsWith(".class") &&
                        !name.endsWith("package-info.class") &&
                        !name.endsWith("module-info.class")) {

                    // Convert path to class name (remove .class and convert / to .)
                    String className = name.substring(0, name.length() - 6).replace('/', '.');
                    classNames.add(className);
                }
            }
        }

        return classNames;
    }

    public Class<? extends Plugin> loadKrakenClient() throws Exception {
        byte[] jarBytes = downloadJar();
        Path tempFile = Files.createTempFile("temp-jar-", ".jar");
        Files.write(tempFile, jarBytes);
        URL jarUrl = tempFile.toUri().toURL();

        try (URLClassLoader classLoader = new URLClassLoader(
                new URL[]{jarUrl},
                Thread.currentThread().getContextClassLoader()
        )) {
            return (Class<? extends Plugin>) classLoader.loadClass("com/kraken/KrakenLoaderPlugin");
        } finally {
            Files.delete(tempFile);
        }
    }
    @Override
    public void close() {
        // No resources to close in this version, but keeping the AutoCloseable interface
        // for consistency and future extensibility
    }
}
