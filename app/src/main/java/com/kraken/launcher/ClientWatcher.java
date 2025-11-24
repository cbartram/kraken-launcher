package com.kraken.launcher;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.SplashScreen;

import javax.inject.Inject;
import java.util.Collections;

/**
 * Waits for the RuneLite's splash screen to be closed. Once closed the client is started and the
 * Kraken loader plugin is initialized.
 */
@Slf4j
public class ClientWatcher {

    @Inject
    private EventBus eventBus;

    @Inject
    private PluginManager pluginManager;

    public void start(Class<?> krakenLoaderPlugin) {
        eventBus.register(this);
        log.info("Starting Client Watcher...");
        new Thread(()-> {
            while(SplashScreen.isOpen()) {
                try{
                    Thread.sleep(100);
                }catch(Exception ex){
                    log.error(ex.getMessage(), ex);
                }
            }
            log.info("Initializing Kraken loader plugin");
            try{
                Plugin krakenClient = pluginManager.loadPlugins(Collections.singletonList(krakenLoaderPlugin), null).get(0);
                pluginManager.setPluginEnabled(krakenClient, true);
                pluginManager.startPlugin(krakenClient);
            } catch(Exception ex) {
                log.error("failed to start Kraken loader plugin", ex);
            }
        }).start();
    }

}