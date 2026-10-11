package com.kraken.launcher;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HealthCheckTest {

    @Test
    public void aHealthyLineExitsZero() {
        String line = "{\"ok\":true,\"kraken\":\"1.18.1\",\"patches\":[]}";

        HealthCheck.Outcome outcome = HealthCheck.outcome(line);

        assertEquals(HealthCheck.HEALTHY, outcome.exitCode);
        assertEquals(line, outcome.line);
    }

    @Test
    public void anUnhealthyOrUnreadableLineExitsOne() {
        assertEquals(HealthCheck.UNHEALTHY, HealthCheck.outcome("{\"ok\":false,\"problems\":[\"x\"]}").exitCode);
        assertEquals(HealthCheck.UNHEALTHY, HealthCheck.outcome("{\"patches\":[]}").exitCode);
        assertEquals(HealthCheck.UNHEALTHY, HealthCheck.outcome("not json").exitCode);
        assertEquals(HealthCheck.UNHEALTHY, HealthCheck.outcome("[true]").exitCode);
    }

    @Test
    public void waitsForTheClientToPublishItsLine() {
        AtomicInteger polls = new AtomicInteger();

        HealthCheck.Outcome outcome = HealthCheck.await(() -> polls.incrementAndGet() < 3 ? null : "{\"ok\":true}",
                Duration.ofSeconds(10));

        assertEquals(HealthCheck.HEALTHY, outcome.exitCode);
        assertEquals(3, polls.get());
    }

    @Test
    public void noLineWithinTheTimeoutExitsTwo() {
        HealthCheck.Outcome outcome = HealthCheck.await(() -> null, Duration.ZERO);

        assertEquals(HealthCheck.NO_REPORT, outcome.exitCode);
        JsonObject line = new Gson().fromJson(outcome.line, JsonObject.class);
        assertFalse(line.get("ok").getAsBoolean());
        assertTrue(line.get("reason").getAsString().startsWith("The Kraken client published no health line within 0 seconds"));
    }

    @Test
    public void failureLinesAreValidJsonWhateverTheMessageHolds() {
        String message = "The Kraken Client is currently offline. (\"injected\" mismatch) \n\nC:\\RuneLite\ttab";

        JsonObject line = new Gson().fromJson(HealthCheck.failure(message), JsonObject.class);

        assertFalse(line.get("ok").getAsBoolean());
        assertEquals(message, line.get("reason").getAsString());
    }
}
