package com.kraken.launcher;

import com.kraken.launcher.bootstrap.BootstrapDownloader;
import com.kraken.launcher.bootstrap.model.Artifact;
import com.kraken.launcher.bootstrap.model.Bootstrap;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.FatalErrorDialog;

import javax.inject.Inject;
import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Hijacks the RuneLite launcher to inject custom client code.
 */
@Slf4j
public class Launcher {

    private static final long CLASSLOADER_POLL_INTERVAL_MS = 100;
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 10;
    private static final String RUNELITE_PACKAGE = "net.runelite.client.rs";
    private static final String LAUNCHER_CLASS = "net.runelite.launcher.Launcher";

    private final ExecutorService executorService;
    private final BootstrapDownloader bootstrapDownloader; // Class internally caches the bootstrap files for both RuneLite and Kraken

    @Inject
    public Launcher(BootstrapDownloader downloader) {
        this.bootstrapDownloader = downloader;
        this.executorService = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "com.kraken.launcher.patcher");
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * Starts the hijack process asynchronously.
     */
    public boolean start() {
        try {
            bootstrapDownloader.downloadKrakenBootstrap();
            bootstrapDownloader.downloadRuneLiteBootstrap();
        } catch (IOException e) {
            log.error("Error fetching one of the bootstrap files, shutting down: ", e);
            return false;
        }
        executorService.submit(this::patchLauncher);
        return true;
    }

    /**
     * Shuts down the executor service gracefully.
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Patches the launcher by hooking into the RuneLite URLClassLoader to load additional custom classes. This
     * loads JAR artifacts from the bootstrap in order to make sure the launcher has all the Kraken dependencies
     * on the classpath when the Kraken Client plugin loads.
     */
    private void patchLauncher() {
        if(bootstrapDownloader.getKrakenBootstrap() == null || bootstrapDownloader.getRuneliteBootstrap() == null) {
            log.error("Kraken or RuneLite Bootstrap file is null. Cannot patch client classpath with unknown dependencies.");
            return;
        }

        // TODO A way to override this (how will users pass configuration in we don't control a launcher UI)?
        if(!checkInjectedClientVersion(bootstrapDownloader)) {
            log.error("RuneLite's injected-client artifact does not match Kraken's hash. RuneLite has pushed an update which needs to be verified.");
            return;
        }

        try {
            ClassLoader classLoader = waitForRuneLiteClassLoader();
            log.info("RuneLite classLoader located");

            if (!(classLoader instanceof URLClassLoader)) {
                throw new IllegalStateException("ClassLoader is not a URLClassLoader");
            }

            URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
            URL launcherJarUrl = resolveJarUrl();

            addUrlToClassLoader(urlClassLoader, launcherJarUrl);
            for(Artifact artifact : bootstrapDownloader.getKrakenBootstrap().getArtifacts()) {
                log.info("Adding to CP: {}", artifact.getName());
                addUrlToClassLoader(urlClassLoader, new URL(artifact.getPath()));

                if(artifact.getName().toLowerCase().startsWith("kraken-client-")) {
                    // Parse version from kraken-client
                    System.setProperty("kraken-client-version", parseVersion(artifact.getName().toLowerCase(), "kraken-client-"));
                }

                if(artifact.getName().toLowerCase().startsWith("kraken-api-")) {
                    System.setProperty("kraken-api-version", parseVersion(artifact.getName().toLowerCase(),  "kraken-api-"));
                }
            }

            // Wait for the RuneLite injector to be created by Guice.
            // Once created it can be used to load the Kraken Client plugin
            new Thread(() -> {
                try {
                    Class<?> runeLiteClass = urlClassLoader.loadClass("net.runelite.client.RuneLite");
                    Method getInjectorMethod = runeLiteClass.getDeclaredMethod("getInjector");

                    Object injector = null;
                    while (injector == null) {
                        injector = getInjectorMethod.invoke(null);
                        if (injector == null) {
                            try {
                                Thread.sleep(25);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                    }

                    Class<?> watcherClass = urlClassLoader.loadClass("com.kraken.launcher.ClientWatcher");
                    Class<?> krakenPluginMainClass = urlClassLoader.loadClass("com.krakenclient.KrakenLoaderPlugin");

                    // Load the Injector INTERFACE to avoid IllegalAccessException on the internal Impl class
                    Class<?> injectorInterface = urlClassLoader.loadClass("com.google.inject.Injector");
                    Method getInstanceMethod = injectorInterface.getMethod("getInstance", Class.class);
                    Object watcherInstance = getInstanceMethod.invoke(injector, watcherClass);


                    // Start the watcher
                    Method startMethod = watcherClass.getMethod("start", Class.class);
                    startMethod.invoke(watcherInstance, krakenPluginMainClass);
                    log.info("Kraken Client injected successfully.");
                } catch (ClassNotFoundException e) {
                    log.error("Class not found during injection (Check classpath/bootstrap): ", e);
                } catch (Exception e) {
                    log.error("Reflection error during injection: ", e);
                }
            }).start();
        } catch (InterruptedException e) {
            log.warn("Client patching process interrupted: ", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Failed to patch RuneLite client: ", e);
        }
    }

    /**
     * Parses a semantic version from a JAR file name in the format <name>-<version>.jar
     * @param name The name of the file to match
     * @param prefix The prefix of the file i.e kraken-client-
     * @return The semantic version i.e 1.2.3
     */
    private String parseVersion(String name, String prefix) {
        String regex = prefix + "(\\d+\\.\\d+\\.\\d+)\\.jar";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);

        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            log.info("Version not found in the string. Defaulting to v1.0.0");
            return "1.0.0";
        }
    }

    /**
     * Verifies that the RuneLite client has not changed or been updated. This gives the Kraken team time to verify if
     * the client is safe to use.
     * @return True if RuneLite's injected client hash matches Kraken's (i.e RuneLite has not pushed a new update).
     */
    private boolean checkInjectedClientVersion(BootstrapDownloader downloader) {
        if(downloader.getRuneliteBootstrap() == null || downloader.getKrakenBootstrap() == null) {
            log.error("Cannot check injected client hash, either Kraken or RuneLite's bootstrap is null");
            return false;
        }

        Bootstrap runeliteBootstrap = downloader.getRuneliteBootstrap();
        Bootstrap krakenBootstrap = downloader.getKrakenBootstrap();

        Artifact injectedClient = Arrays.stream(runeliteBootstrap.getArtifacts())
                .filter((a) -> a.getName().contains("injected-client"))
                .findFirst()
                .orElse(null);

        if (injectedClient != null) {
            Artifact hook = Arrays.stream(runeliteBootstrap.getArtifacts())
                    .filter((a) -> a.getName().contains("rlicn-"))
                    .findFirst()
                    .orElse(null);

            if (hook == null) {
                SwingUtilities.invokeLater(() -> (new FatalErrorDialog("The Kraken Client is currently offline. (RLICN artifact missing) \n\nThis is likely due to RuneLite pushing a new client update that needs to be checked by the Kraken team to ensure it keeps the client safe and undetected. \n\nIf you would like to run vanilla RuneLite from this launcher, set runelite mode in the runelite (configure) window or use the --rl arg or skip this message AT YOUR OWN RISK by checking the \"Skip RuneLite Update Check\" checkbox.")).open());
                return false;
            }

            log.info("kraken bootstrap hash: {} injected client hash: {}", krakenBootstrap.getHash(), injectedClient.getHash());
            if (!krakenBootstrap.getHash().equalsIgnoreCase(injectedClient.getHash())) {
                SwingUtilities.invokeLater(() -> (new FatalErrorDialog("The Kraken Client is currently offline. (injected version mismatch) \n\nThis is likely due to RuneLite pushing a new client update that needs to be checked by the Kraken team to ensure it keeps the client safe and undetected. \n\nIf you would like to run vanilla RuneLite from this launcher, set runelite mode in the runelite (configure) window or use the --rl arg or skip this message AT YOUR OWN RISK by checking the \"Skip RuneLite Update Check\" checkbox.")).open());
                return false;

                // If RuneLite tries to change anything with regards to these DLL hooks we should fail the client startup
                // as something fishy is going on
            } else if (krakenBootstrap.getHookHash() != null && krakenBootstrap.getHookHash().equalsIgnoreCase(hook.getHash())) {
                return true;
            }

            SwingUtilities.invokeLater(() -> (new FatalErrorDialog("The Kraken Client is currently offline. (RLICN hash mismatch) \n\nThis is likely due to RuneLite pushing a new client update that needs to be checked by the Kraken team to ensure it keeps the client safe and undetected. \n\nIf you would like to run vanilla RuneLite from this launcher, set runelite mode in the runelite (configure) window or use the --rl arg or skip this message AT YOUR OWN RISK by checking the \"Skip RuneLite Update Check\" checkbox.")).open());
        }


        log.error("Could not locate RuneLite's injected-client artifact in bootstrap or Kraken's client in Kraken's bootstrap");
        return false;
    }


    /**
     * Polls for the RuneLite ClassLoader until it's available.
     */
    private ClassLoader waitForRuneLiteClassLoader() throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            ClassLoader classLoader = (ClassLoader) UIManager.get("ClassLoader");
            if(classLoader != null) {
                for (Package pack : classLoader.getDefinedPackages()) {
                    if (pack.getName().equals(RUNELITE_PACKAGE)) {
                        return classLoader;
                    }
                }
            }

            Thread.sleep(Launcher.CLASSLOADER_POLL_INTERVAL_MS);
        }
        throw new InterruptedException("Interrupted while waiting for ClassLoader");
    }

    /**
     * Resolves the URL of the Kraken launcher JAR file.
     */
    private URL resolveJarUrl() throws Exception {
        URI uri = Launcher.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI();

        if (uri.getPath().endsWith("classes/")) {
            uri = uri.resolve("..");
        }

        if (!uri.getPath().endsWith(".jar")) {
            uri = uri.resolve("kraken-launcher-1.0.0-fat.jar");
        }

        return uri.toURL();
    }

    /**
     * Adds a URL to the URLClassLoader using reflection.
     */
    private void addUrlToClassLoader(URLClassLoader classLoader, URL url) throws Exception {
        Method addUrl = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        addUrl.setAccessible(true);
        addUrl.invoke(classLoader, url);
    }


    public static void main(String[] args) {
        log.info("Starting Kraken Launcher");
        System.setProperty("runelite.launcher.nojvm", "true");
        System.setProperty("runelite.launcher.reflect", "true");

        Launcher launcher = new Launcher(new BootstrapDownloader());

        if(!launcher.start()) {
            log.info("Kraken Launcher failed to start, see error messages above.");
            return;
        }

        try {
            Class<?> launcherClass = Class.forName(LAUNCHER_CLASS);
            launcherClass.getMethod("main", String[].class).invoke(null, (Object) args);
        } catch (Exception e) {
            log.error("Failed to start RuneLite launcher", e);
            launcher.shutdown();
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(launcher::shutdown, "com.kraken.launcher.shutdown"));
    }
}