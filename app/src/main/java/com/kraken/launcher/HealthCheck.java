package com.kraken.launcher;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * The --health-check launch: Kraken starts without the launcher UI or any dialog, this waits for the Kraken client to
 * publish its health line after patching, prints it, and exits with a status code. Patching runs when the Kraken
 * plugin starts, before login, so the check needs no account.
 * <p>
 * The client and launcher share this JVM but not classes, so the client publishes the line as the
 * {@value #STATUS_PROPERTY} system property, and {@value #ACTIVE_PROPERTY} tells it to enable every patch.
 * </p>
 * Exit codes: {@value #HEALTHY} the client reported ok, {@value #UNHEALTHY} it reported a failed patch or mismatched
 * hooks, {@value #NO_REPORT} it reported nothing within the timeout, {@value #NOT_STARTED} the launcher refused to
 * start it (a bootstrap could not be fetched, or the RuneLite update safety check failed).
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HealthCheck {

    static final String ACTIVE_PROPERTY = "kraken.healthCheck";
    static final String STATUS_PROPERTY = "kraken.health";
    static final String LINE_PREFIX = "KRAKEN_HEALTH ";

    static final int HEALTHY = 0;
    static final int UNHEALTHY = 1;
    static final int NO_REPORT = 2;
    static final int NOT_STARTED = 3;

    private static final Duration TIMEOUT = Duration.ofMinutes(5);
    private static final long POLL_MILLIS = 500;

    private static volatile boolean active;

    /**
     * The outcome of a health check: the exit code and the line to print.
     */
    static final class Outcome {
        final int exitCode;
        final String line;

        Outcome(int exitCode, String line) {
            this.exitCode = exitCode;
            this.line = line;
        }
    }

    /**
     * Starts the health check: marks it active for the launcher and the client, and waits on a daemon thread for the
     * client's health line.
     */
    static void start() {
        active = true;
        System.setProperty(ACTIVE_PROPERTY, "true");
        log.info("Health check: starting Kraken and waiting up to {} seconds for the client's health line", TIMEOUT.getSeconds());

        Thread watchdog = new Thread(() -> finish(await(() -> System.getProperty(STATUS_PROPERTY), TIMEOUT)),
                "com.kraken.launcher.health-check");
        watchdog.setDaemon(true);
        watchdog.start();
    }

    /**
     * @return true during a --health-check launch, when failures must exit instead of showing a dialog.
     */
    static boolean isActive() {
        return active;
    }

    /**
     * Ends the health check because the launcher will not start the client.
     *
     * @param message the reason, as it would have been shown to the user.
     */
    static void launcherFailed(String message) {
        finish(new Outcome(NOT_STARTED, failure("The launcher did not start the client: " + message)));
    }

    /**
     * Waits for the client's health line.
     *
     * @param status  reads the published line, or null while there is none.
     * @param timeout how long to wait.
     * @return the outcome for the line, or {@value #NO_REPORT} when none arrived in time.
     */
    static Outcome await(Supplier<String> status, Duration timeout) {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (true) {
            String line = status.get();
            if (line != null) {
                return outcome(line);
            }
            if (System.nanoTime() >= deadline) {
                return new Outcome(NO_REPORT, failure("The Kraken client published no health line within "
                        + timeout.getSeconds() + " seconds; see client.log"));
            }
            try {
                Thread.sleep(POLL_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new Outcome(NO_REPORT, failure("Interrupted while waiting for the Kraken client's health line"));
            }
        }
    }

    /**
     * @param line the client's health JSON.
     * @return {@value #HEALTHY} when the line says {@code "ok":true}, otherwise {@value #UNHEALTHY}.
     */
    static Outcome outcome(String line) {
        try {
            JsonObject health = new Gson().fromJson(line, JsonObject.class);
            JsonElement ok = health == null ? null : health.get("ok");
            boolean healthy = ok != null && ok.isJsonPrimitive() && ok.getAsBoolean();
            return new Outcome(healthy ? HEALTHY : UNHEALTHY, line);
        } catch (JsonParseException | IllegalStateException | ClassCastException e) {
            return new Outcome(UNHEALTHY, line);
        }
    }

    /**
     * Writes the launcher's own failure line by hand: RuneLite.jar's minimized Gson, which wins on the installed
     * launcher's class path, is only safe to read with.
     */
    static String failure(String reason) {
        StringBuilder escaped = new StringBuilder();
        for (char c : reason.toCharArray()) {
            if (c == '"' || c == '\\') {
                escaped.append('\\').append(c);
            } else if (c == '\n') {
                escaped.append("\\n");
            } else if (c < 0x20) {
                escaped.append(String.format("\\u%04x", (int) c));
            } else {
                escaped.append(c);
            }
        }
        return "{\"ok\":false,\"reason\":\"" + escaped + "\"}";
    }

    private static void finish(Outcome outcome) {
        if (outcome.exitCode == HEALTHY) {
            log.info("{}{}", LINE_PREFIX, outcome.line);
        } else {
            log.error("{}{}", LINE_PREFIX, outcome.line);
        }
        System.out.println(LINE_PREFIX + outcome.line);
        System.out.flush();
        log.info("Health check finished, exiting with {}", outcome.exitCode);
        System.exit(outcome.exitCode);
    }
}
