package com.kraken.launcher;

import com.google.gson.*;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Installer {

    private static final String RUNELITE_DIR = System.getenv("LOCALAPPDATA") + "\\RuneLite";
    private static final String CONFIG_FILE = RUNELITE_DIR + "\\config.json";
    private static final String TARGET_MAIN_CLASS = "com.kraken.launcher.Launcher";

    public static void main(String[] args) {
        try {
            // 1. Verify we are on Windows (since paths are hardcoded for AppData)
            if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                showError("This installer is designed for Windows.");
                return;
            }

            File currentJar = new File(Installer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            String jarName = currentJar.getName();

            File targetDir = new File(RUNELITE_DIR);
            if (!targetDir.exists()) {
                showError("RuneLite installation not found at: " + RUNELITE_DIR + "\nPlease install RuneLite first.");
                return;
            }
            File targetJar = new File(targetDir, jarName);

            // We perform a copy even if it exists to update to the new version
            if (!currentJar.equals(targetJar)) {
                Files.copy(currentJar.toPath(), targetJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            updateConfigJson(jarName);
            JOptionPane.showMessageDialog(null,
                    "Kraken Launcher installed successfully!\n\n" +
                            "You can now launch RuneLite normally.",
                    "Installation Complete",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Installation failed: " + e.getMessage());
        }
    }

    private static void updateConfigJson(String jar) throws IOException {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            throw new IOException("config.json not found in RuneLite directory.");
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject configObject;

        try (FileReader reader = new FileReader(configFile)) {
            configObject = JsonParser.parseReader(reader).getAsJsonObject();
        }

        configObject.addProperty("mainClass", TARGET_MAIN_CLASS);
        if (configObject.has("classPath")) {
            JsonArray classPath = configObject.getAsJsonArray("classPath");
            boolean jarExists = false;

            // Check if jar is already in classpath to avoid duplicates
            for (JsonElement element : classPath) {
                if (element.getAsString().equals(jar)) {
                    jarExists = true;
                    break;
                }
            }

            if (!jarExists) {
                classPath.add(jar);
            }
        } else {
            // Create classpath if it somehow doesn't exist
            JsonArray classPath = new JsonArray();
            classPath.add("RuneLite.jar");
            classPath.add(jar);
            configObject.add("classPath", classPath);
        }

        // Write changes back to file
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(configObject, writer);
        }
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Installer Error", JOptionPane.ERROR_MESSAGE);
    }
}