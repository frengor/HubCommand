package com.fren_gor.hubcommand;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.bstats.charts.SimplePie;

public class Main extends Plugin {

    private static final int BSTATS_ID = 2826;

    private static Main m;

    public static Main getInstance() {
        return m;
    }

    @Override
    public void onEnable() {
        m = this;
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ConfigManager.updateVersion();

        getProxy().getPluginManager().registerCommand(this, new Hub(ConfigManager.needsPermission()));

        Metrics metrics = new Metrics(this, BSTATS_ID);
        metrics.addCustomChart(new SimplePie("requires_permission", () -> ConfigManager.needsPermission() ? "Yes" : "No"));
        metrics.addCustomChart(new SimplePie("has_disabled_servers", () -> ConfigManager.hasDisabledServers() ? "Yes" : "No"));
    }
}
