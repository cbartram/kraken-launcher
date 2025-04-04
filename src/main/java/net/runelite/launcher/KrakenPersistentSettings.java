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

public class KrakenPersistentSettings {
    private static final String KRAKEN_SETTINGS = "krakenprefs.json";
    boolean rlMode = false;
    boolean hBootstrap = false;
    boolean skipUpdatedClientCheck = false;
    String proxy = "";
    String maxMem = "1G";

    void apply(OptionSet options) {
        if (options.has("rl")) {
            this.rlMode = Launcher.krakenData.rlMode = true;
        } else {
            Launcher.krakenData.rlMode = this.rlMode;
        }

        if (options.has("hBootstrap")) {
            this.hBootstrap = Launcher.krakenData.hBootstrap = true;
        } else {
            Launcher.krakenData.hBootstrap = this.hBootstrap;
        }

        if (options.has("skipUpdatedClientCheck")) {
            this.skipUpdatedClientCheck = Launcher.krakenData.skipUpdatedClientCheck = true;
        } else {
            Launcher.krakenData.skipUpdatedClientCheck = this.skipUpdatedClientCheck;
        }

        if (options.has("proxy")) {
            this.proxy = Launcher.krakenData.proxy = String.valueOf(options.valueOf("proxy"));
        } else {
            Launcher.krakenData.proxy = this.proxy;
        }

        if (options.has("maxmemory")) {
            this.maxMem = Launcher.krakenData.maxMemory = String.valueOf(options.valueOf("maxmemory"));
        } else {
            Launcher.krakenData.maxMemory = this.maxMem;
        }

        if (options.has("krakenprofile")) {
            Launcher.krakenData.krakenProfile = String.valueOf(options.valueOf("krakenprofile"));
        }

        if (options.has("remote-debug")) {
            Launcher.krakenData.startDebugger = true;
        }

    }

    static KrakenPersistentSettings loadSettings() {
        File settingsFile = new File(RUNELITE_DIR, "kraken/krakenprefs.json");

        try (InputStreamReader in = new InputStreamReader(new FileInputStream(settingsFile), StandardCharsets.UTF_8)) {
            KrakenPersistentSettings settings = new Gson().fromJson(in, KrakenPersistentSettings.class);
            return MoreObjects.firstNonNull(settings, new KrakenPersistentSettings());
        } catch (JsonParseException | IOException var6) {
            KrakenPersistentSettings launcherSettings = new KrakenPersistentSettings();
            saveSettings(launcherSettings);
            return launcherSettings;
        }
    }

    static void saveSettings(KrakenPersistentSettings settings) {
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
