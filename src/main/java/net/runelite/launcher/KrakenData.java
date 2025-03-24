package net.runelite.launcher;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Data
public class KrakenData {
    public static final boolean DEV = false;
    public static final String KRAKEN_BOOTSTRAP = "http://kraken-bootstrap-static.s3-website-us-east-1.amazonaws.com/bootstrap.json";
    public static final String H_BOOTSTRAP = "https://osrsplugins.com/static/launcher/bootstrap.json";
    public static final File KRAKEN_DIR;
    public static final String OUTDATED_MESSAGE_FALLBACK = "The Kraken client is currently offline. \n\nThis is likely due to RuneLite pushing a new client update that needs to be checked by the Kraken team before we can re-open the client. \n\nKeep an eye out on announcement channels in the discord for updates, and please do not message staff members asking why it does not load. \n\nIf you would like to run vanilla RuneLite from this launcher, set runelite mode in the runelite (configure) window or use the --rl arg.";
    private static KrakenBootstrap krakenBootstrap;
    String currentLauncherVersion = "3.0.0";
    String proxy = "";
    String krakenProfile = "";
    String maxMemory = "3G";
    boolean rlMode = false;
    boolean hBootstrap = false;
    boolean skipUpdatedClientCheck = false;
    boolean startDebugger = false;

    static {
        KRAKEN_DIR = new File(Launcher.RUNELITE_DIR, "kraken");
        krakenBootstrap = null;
    }

    public static KrakenBootstrap getKrakenBootstrap(HttpClient httpClient, boolean hBootstrap) throws IOException {
        if (krakenBootstrap != null && !hBootstrap) {
            return krakenBootstrap;
        } else {

            HttpRequest bootstrapReq;

            if (hBootstrap) {
                log.info("Getting H bootstrap from: " + H_BOOTSTRAP);
                bootstrapReq = HttpRequest.newBuilder().uri(URI.create(H_BOOTSTRAP)).GET().build();
            } else {
                log.info("Getting Kraken bootstrap from: " + KRAKEN_BOOTSTRAP);
                bootstrapReq = HttpRequest.newBuilder().uri(URI.create(KRAKEN_BOOTSTRAP)).GET().build();
            }

            HttpResponse<String> resp;
            try {
                resp = httpClient.send(bootstrapReq, HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException e) {
                throw new IOException(e);
            }

            if (resp.statusCode() != 200) {
                throw new IOException("Unable to download bootstrap from url (status code " + resp.statusCode() + "): " + resp.body());
            } else {
                krakenBootstrap = new Gson().fromJson(resp.body(), KrakenBootstrap.class);
                return krakenBootstrap;
            }
        }
    }
}
