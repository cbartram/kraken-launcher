package com.kraken.launcher;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.externalplugins.ExternalPluginManager;
import net.runelite.client.ui.SplashScreen;

import javax.inject.Inject;
import java.lang.reflect.Method;

/**
 * Waits for the RuneLite's splash screen to be closed. Once closed the client is started and the
 * Kraken loader plugin is initialized.
 */
@Slf4j
public class ClientWatcher {

    @Inject
    private EventBus eventBus;

    @Inject
    private ExternalPluginManager externalPluginManager;

    public void start(Class<?> krakenLoaderPlugin) {
        eventBus.register(this);
        log.info("Starting Client Watcher...");
        new Thread(()->{
            while(SplashScreen.isOpen()) {
                try{
                    Thread.sleep(100);
                }catch(Exception ex){
                    log.error(ex.getMessage(), ex);
                }
            }
            log.info("RuneLite splash screen completed, initializing Kraken client");
            try{
                Method loadBuiltinMethod = externalPluginManager.getClass().getMethod("loadBuiltin", Class[].class);
                loadBuiltinMethod.invoke(null, (Object) new Class[]{krakenLoaderPlugin});
            } catch(Exception ex) {
                log.error("failed to start Kraken loader plugin", ex);
            }
        }).start();
    }

}