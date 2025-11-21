package net.runelite.launcher;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import joptsimple.OptionSet;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static net.runelite.launcher.Launcher.RUNELITE_DIR;

@Slf4j
@Data
public class PersistedPreferences {
    private static final String KRAKEN_SETTINGS = "krakenprefs.json";

    private String proxy = "";
    private String maxMem = "1G";
    private String krakenProfile = "";
    private String maxMemory = "3G";
    private boolean rlMode = false;
    private boolean skipUpdatedClientCheck = false;
    private boolean startDebugger = false;

    public void applyTo(OptionSet options) {
        rlMode = options.has("rl") || this.rlMode;
        skipUpdatedClientCheck = options.has("skipUpdatedClientCheck") || this.skipUpdatedClientCheck;
        proxy = options.has("proxy") ? String.valueOf(options.valueOf("proxy")) : this.proxy;
        maxMemory = options.has("maxmemory") ? String.valueOf(options.valueOf("maxmemory")) : this.maxMem;

        if (options.has("krakenprofile")) {
            krakenProfile = String.valueOf(options.valueOf("krakenprofile"));
        }

        if (options.has("remote-debug")) {
            startDebugger = true;
        }
    }

    public static PersistedPreferences loadSettings() {
        File settingsFile = new File(RUNELITE_DIR, "kraken/krakenprefs.json");

        if (!settingsFile.exists()) {
            return createDefaultSettings();
        }

        try (Reader reader = Files.newBufferedReader(settingsFile.toPath(), StandardCharsets.UTF_8)) {
            PersistedPreferences settings = new Gson().fromJson(reader, PersistedPreferences.class);
            return settings != null ? settings : createDefaultSettings();
        } catch (JsonParseException | IOException e) {
            log.error("Failed to load settings: " + e.getMessage());
            return createDefaultSettings();
        }
    }

    private static PersistedPreferences createDefaultSettings() {
        PersistedPreferences settings = new PersistedPreferences();
        saveSettings(settings);
        return settings;
    }

    static void saveSettings(PersistedPreferences settings) {
        File krakenDir = new File(RUNELITE_DIR, "kraken");
        krakenDir.mkdirs();

        File settingsFile = new File(krakenDir, KRAKEN_SETTINGS);
        File tmpFile = null;

        try {
            tmpFile = File.createTempFile(KRAKEN_SETTINGS, ".json", krakenDir);

            try (BufferedWriter writer = Files.newBufferedWriter(tmpFile.toPath(), StandardCharsets.UTF_8)) {
                new Gson().toJson(settings, writer);
            }

            try {
                Files.move(tmpFile.toPath(), settingsFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmpFile.toPath(), settingsFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save settings", e);
        } finally {
            if (tmpFile != null && tmpFile.exists()) {
                tmpFile.delete();
            }
        }
    }
}
