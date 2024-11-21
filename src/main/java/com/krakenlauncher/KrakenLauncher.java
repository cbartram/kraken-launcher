package com.krakenlauncher;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import net.runelite.client.plugins.Plugin;

import javax.swing.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class KrakenLauncher {

    private static final String KRAKEN_CLIENT_PLUGIN_NAME = "com.kraken.KrakenLoaderPlugin";

    public static boolean checkJavaVersion() {
        String javaHome = System.getProperty("java.home");
        String javaVersion = System.getProperty("java.version");

        // Extract the major version number from the java.version property
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(javaVersion);
        if (matcher.find()) {
            int majorVersion = Integer.parseInt(matcher.group(1));

            if (majorVersion > 11) {
                showUnsupportedVersionDialog(javaHome, javaVersion);
                return false;
            } else {
                log.info("Java version {} is supported.", javaVersion);
                return true;
            }
        } else {
            log.error("Unable to determine Java version from: {}", javaVersion);
            return false;
        }
    }

    private static void showUnsupportedVersionDialog(String javaHome, String javaVersion) {
        JOptionPane.showMessageDialog(
                null,
                "Only Java 11 is supported. You are using Java " + javaVersion + " located at " + javaHome + ". Download Java 11 JDK here: https://adoptium.net/temurin/releases/?version=11",
                "Unsupported Java Version",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SplashScreen.init();
        checkJavaVersion();
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);

        // Load the JAR file from S3
        try {
            SplashScreen.stage(0.02, null, "Downloading Client");
            Map<String, Class<?>> krakenClientClasses = new JarLoader().loadKrakenClientClasses();
            Thread.sleep(400);
            SplashScreen.stage(0.50, null, "Loading Client");
            log.info("Kraken Client class loaded successfully.");
            Thread.sleep(600);
            ExternalPluginManager.loadBuiltin((Class<? extends Plugin>) krakenClientClasses.get(KRAKEN_CLIENT_PLUGIN_NAME));
            SplashScreen.stage(1.00, null, "Starting Kraken Client");
            log.info("Starting RuneLite");

            Thread.sleep(1200);
            SplashScreen.stop();
            RuneLite.main(args);
        } catch (Exception e) {
            log.error("General exception thrown while attempting to launch Kraken Client: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
