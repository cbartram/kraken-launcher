package net.runelite.launcher;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Collection;
import java.util.List;


@Slf4j
class ReflectionLauncher {

    static void launch(BootstrapDownloader downloader, List<File> classpath, Collection<String> clientArgs) throws MalformedURLException {
        launch(downloader, classpath, clientArgs, "");
    }

    static void launch(BootstrapDownloader downloader, List<File> classpath, Collection<String> clientArgs, String proxyString) throws MalformedURLException {
        clientArgs.add("--insecure-write-credentials");
        clientArgs.add("--developer-mode");

        // Configure proxy if provided
        if (proxyString != null && !proxyString.trim().isEmpty()) {
            log.info("Using proxy: {}", proxyString);
            configureProxy(proxyString);
        }

        URL[] jarUrls = new URL[classpath.size()];
        int i = 0;
        for (var file : classpath) {
            log.debug("Adding jar: {}", file);
            jarUrls[i++] = file.toURI().toURL();
        }

        ClassLoader parent = ClassLoader.getPlatformClassLoader();
        URLClassLoader loader = new URLClassLoader(jarUrls, parent);

        // Swing requires the UIManager ClassLoader to be set if the LAF
        // is not in the boot classpath
        UIManager.put("ClassLoader", loader);

        Thread thread = new Thread(() -> {
            try {
                loader.setDefaultAssertionStatus(true);
                Class<?> mainClass = loader.loadClass(LauncherProperties.getMain());

                // Before we invoke the main class, check to see if RuneLite mode is disabled. If so we are clear
                // to load Kraken plugins
                if(!downloader.getPreferences().isRlMode()) {
                    log.info("RuneLite mode: disabled. Loading Kraken Plugin class");
                    Class<?> krakenPluginMainClass = loader.loadClass("com.krakenclient.KrakenLoaderPlugin");
                    Class<?> externalPluginManagerClass = loader.loadClass("net.runelite.client.externalplugins.ExternalPluginManager");
                    Method loadBuiltinMethod = externalPluginManagerClass.getMethod("loadBuiltin", Class[].class);
                    loadBuiltinMethod.invoke(null, (Object) new Class[]{krakenPluginMainClass});
                } else {
                    log.info("RuneLite mode: enabled. Skipping Kraken classes.");
                }

                Method main = mainClass.getMethod("main", String[].class);
                main.invoke(null, (Object) clientArgs.toArray(new String[0]));
            } catch (Exception ex) {
                log.error("Unable to launch client", ex);
            }
        });
        thread.setName("RuneLite");
        thread.start();
    }

    private static void configureProxy(String proxyString) {
        try {
            String[] parts = proxyString.split(":");
            if (parts.length != 2 && parts.length != 4) {
                log.error("Invalid proxy format. Expected IP:PORT or IP:PORT:USER:PASS, got: {}", proxyString);
                return;
            }

            String proxyHost = parts[0];
            String proxyPort = parts[1];
            String proxyUser = parts.length == 4 ? parts[2] : "";
            String proxyPass = parts.length == 4 ? parts[3] : "";

            log.info("Configuring SOCKS5 proxy: {}:{}", proxyHost, proxyPort);

            // Set SOCKS proxy (this handles both TCP and UDP traffic)
            System.setProperty("socksProxyHost", proxyHost);
            System.setProperty("socksProxyPort", proxyPort);
            System.setProperty("socksProxyVersion", "5");

            // For SOCKS5, we don't set HTTP/HTTPS proxy properties as they would take precedence
            // and bypass the SOCKS proxy

            // Configure SOCKS authentication if credentials provided
            if (!proxyUser.isEmpty() && !proxyPass.isEmpty()) {
                System.setProperty("java.net.socks.username", proxyUser);
                System.setProperty("java.net.socks.password", proxyPass);

                // Set up authenticator for SOCKS authentication
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        if (getRequestorType() == RequestorType.PROXY) {
                            // Check if this is a SOCKS proxy request
                            String protocol = getRequestingProtocol();
                            log.info("Requesting proxy protocol: {}", protocol);
                            if (protocol != null && protocol.toLowerCase().contains("socks")) {
                                return new PasswordAuthentication(proxyUser, proxyPass.toCharArray());
                            }
                        }
                        return null;
                    }
                });

                log.info("SOCKS5 authentication configured for user: {}", proxyUser);
            } else {
                log.info("SOCKS5 proxy configured without authentication");
            }

        } catch (Exception e) {
            log.error("Failed to configure SOCKS5 proxy: {}", e.getMessage(), e);
        }
    }
}