package com.fren_gor.hubcommand;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

	private static Main m;

	public static Main getInstance() {
		return m;
	}

	@Override
	public void onEnable() {
		m = this;
		if (!getDataFolder().exists())
			getDataFolder().mkdirs();
		getProxy().getPluginManager().registerCommand(this, new Hub());
		File file = new File(getDataFolder(), "config.yml");

		if (!file.exists()) {
			try (InputStream in = getResourceAsStream("config.yml")) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ConfigManager.updateVersion();
		
	}
}
