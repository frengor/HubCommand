package com.fren_gor.hubcommand.velocity;

import com.fren_gor.hubcommand.ConfigManager;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.bstats.charts.SimplePie;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "hubcommand",
        name = "HubCommand",
        version = "1.3.0",
        url = "https://github.com/frengor/HubCommand",
        description = "Simple /hub command for BungeeCord and Velocity",
        authors = {"fren_gor"})
public class MainVelocity {

    private static final int BSTATS_ID = 26711;

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final Metrics.Factory metricsFactory;
    private ConfigManager configManager;

    @Inject
    public MainVelocity(ProxyServer server, Logger logger, Metrics.Factory metricsFactory, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            configManager = new ConfigManager(dataDirectory.toFile());
        } catch (Exception e) {
            logger.error("Couldn't load configuration", e);
            return;
        }

        CommandManager commandManager = server.getCommandManager();
        CommandMeta meta = commandManager.metaBuilder("hub").plugin(this).build();
        commandManager.register(meta, Commands.getCommand(this));

        Metrics metrics = metricsFactory.make(this, BSTATS_ID);
        metrics.addCustomChart(new SimplePie("requires_permission", () -> configManager.needsPermission() ? "Yes" : "No"));
        metrics.addCustomChart(new SimplePie("has_disabled_servers", () -> configManager.hasDisabledServers() ? "Yes" : "No"));
    }

    public ProxyServer getProxyServer() {
        return server;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
