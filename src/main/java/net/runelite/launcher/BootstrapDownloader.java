package net.runelite.launcher;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class BootstrapDownloader {
    public static final String KRAKEN_BOOTSTRAP = "https://minio.kraken-plugins.com/kraken-bootstrap-static/bootstrap.json";
    private static BootstrapDependencies bootstrap = null;

    @Getter
    @Setter
    private PersistedPreferences preferences;

    /**
     * Downloads the bootstrap file from the server or returns it if the file is already cached in memory. This
     * method also deserializes the data into an object.
     * @param httpClient HttpClient to fetch the bootstrap
     * @return BootstrapDependencies object
     * @throws IOException IOException if there are failures
     */
    public static BootstrapDependencies downloadBootstrap(HttpClient httpClient) throws IOException {
        if (bootstrap != null) return bootstrap;

        HttpRequest bootstrapReq;
        log.info("Getting Kraken bootstrap from: " + KRAKEN_BOOTSTRAP);
        bootstrapReq = HttpRequest.newBuilder().uri(URI.create(KRAKEN_BOOTSTRAP)).GET().build();

        HttpResponse<String> resp;
        try {
            resp = httpClient.send(bootstrapReq, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            log.error("Failed to get bootstrap json file: ", e);
            return null;
        }

        if (resp.statusCode() != 200) {
            throw new IOException("Unable to download bootstrap from url (status code " + resp.statusCode() + "): " + resp.body());
        } else {
            bootstrap = new Gson().fromJson(resp.body(), BootstrapDependencies.class);
            return bootstrap;
        }
    }
}
