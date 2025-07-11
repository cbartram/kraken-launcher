/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.launcher;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

@Slf4j
class ReflectionLauncher
{
	static void launch(List<File> classpath, Collection<String> clientArgs) throws MalformedURLException
	{
		clientArgs.add("--insecure-write-credentials");
		URL[] jarUrls = new URL[classpath.size()];
		int i = 0;
		for (var file : classpath)
		{
			log.debug("Adding jar: {}", file);
			jarUrls[i++] = file.toURI().toURL();
		}

		ClassLoader parent = ClassLoader.getPlatformClassLoader();
		URLClassLoader loader = new URLClassLoader(jarUrls, parent);

		// Swing requires the UIManager ClassLoader to be set if the LAF
		// is not in the boot classpath
		UIManager.put("ClassLoader", loader);

		Thread thread = new Thread(() ->
		{
			try
			{
				loader.setDefaultAssertionStatus(true);
				Class<?> mainClass = loader.loadClass(LauncherProperties.getMain());

				// Before we invoke the main class, check to see if RuneLite mode is disabled. If so we are clear
				// to load Kraken plugins
				if(!Launcher.krakenData.rlMode) {
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
			}
			catch (Exception ex)
			{
				log.error("Unable to launch client", ex);
			}
		});
		thread.setName("RuneLite");
		thread.start();
	}
}
