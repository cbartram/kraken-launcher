package net.runelite.launcher;

import com.google.common.base.MoreObjects;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import joptsimple.OptionSet;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static net.runelite.launcher.Launcher.RUNELITE_DIR;

public class PersistedPreferences {
    private static final String KRAKEN_SETTINGS = "krakenprefs.json";
    boolean rlMode = false;
    boolean skipUpdatedClientCheck = false;
    String proxy = "";
    String maxMem = "1G";

    void apply(OptionSet options) {
        if (options.has("rl")) {
            this.rlMode = Launcher.BOOTSTRAP_DOWNLOADER.rlMode = true;
        } else {
            Launcher.BOOTSTRAP_DOWNLOADER.rlMode = this.rlMode;
        }

        if (options.has("skipUpdatedClientCheck")) {
            this.skipUpdatedClientCheck = Launcher.BOOTSTRAP_DOWNLOADER.skipUpdatedClientCheck = true;
        } else {
            Launcher.BOOTSTRAP_DOWNLOADER.skipUpdatedClientCheck = this.skipUpdatedClientCheck;
        }

        if (options.has("proxy")) {
            this.proxy = Launcher.BOOTSTRAP_DOWNLOADER.proxy = String.valueOf(options.valueOf("proxy"));
        } else {
            Launcher.BOOTSTRAP_DOWNLOADER.proxy = this.proxy;
        }

        if (options.has("maxmemory")) {
            this.maxMem = Launcher.BOOTSTRAP_DOWNLOADER.maxMemory = String.valueOf(options.valueOf("maxmemory"));
        } else {
            Launcher.BOOTSTRAP_DOWNLOADER.maxMemory = this.maxMem;
        }

        if (options.has("krakenprofile")) {
            Launcher.BOOTSTRAP_DOWNLOADER.krakenProfile = String.valueOf(options.valueOf("krakenprofile"));
        }

        if (options.has("remote-debug")) {
            Launcher.BOOTSTRAP_DOWNLOADER.startDebugger = true;
        }

    }

    static PersistedPreferences loadSettings() {
        File settingsFile = new File(RUNELITE_DIR, "kraken/krakenprefs.json");

        try (InputStreamReader in = new InputStreamReader(new FileInputStream(settingsFile), StandardCharsets.UTF_8)) {
            PersistedPreferences settings = new Gson().fromJson(in, PersistedPreferences.class);
            return MoreObjects.firstNonNull(settings, new PersistedPreferences());
        } catch (JsonParseException | IOException var6) {
            PersistedPreferences launcherSettings = new PersistedPreferences();
            saveSettings(launcherSettings);
            return launcherSettings;
        }
    }

    static void saveSettings(PersistedPreferences settings) {
        File krakenDir = new File(RUNELITE_DIR, "kraken");

        if(!krakenDir.exists()) {
            krakenDir.mkdir();
        }

        File settingsFile = new File(RUNELITE_DIR, "kraken/krakenprefs.json").getAbsoluteFile();

        try {
            File tmpFile = File.createTempFile(KRAKEN_SETTINGS, "json");
            Gson gson = new Gson();

            try (
                    FileOutputStream fout = new FileOutputStream(tmpFile);
                    FileChannel channel = fout.getChannel();
                    OutputStreamWriter writer = new OutputStreamWriter(fout, StandardCharsets.UTF_8);
            ) {
                channel.lock();
                gson.toJson(settings, writer);
                writer.flush();
                channel.force(true);
            }

            try {
                Files.move(tmpFile.toPath(), settingsFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException var12) {
                Files.move(tmpFile.toPath(), settingsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
            settingsFile.delete();
        }
    }
}
