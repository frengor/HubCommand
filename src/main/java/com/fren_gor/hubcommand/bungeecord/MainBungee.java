package com.fren_gor.hubcommand.bungeecord;

import com.fren_gor.hubcommand.ConfigManager;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.bstats.charts.SimplePie;

public class MainBungee extends Plugin {

    private static final int BSTATS_ID = 2826;

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(getDataFolder());

        getProxy().getPluginManager().registerCommand(this, new Commands(this, configManager));

        Metrics metrics = new Metrics(this, BSTATS_ID);
        metrics.addCustomChart(new SimplePie("requires_permission", () -> configManager.needsPermission() ? "Yes" : "No"));
        metrics.addCustomChart(new SimplePie("has_disabled_servers", () -> configManager.hasDisabledServers() ? "Yes" : "No"));
    }
}
