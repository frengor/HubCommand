package com.fren_gor.hubcommand.bungeecord;

import com.fren_gor.hubcommand.ConfigManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Commands extends Command {

    private final MainBungee main;
    private final ConfigManager configManager;

    public Commands(MainBungee main, ConfigManager configManager) {
        super("hub", configManager.needsPermission() ? "hubcommand.command" : null);
        this.main = main;
        this.configManager = configManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("You must be a player to do /hub"));
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer) sender;
        String currentServer = p.getServer().getInfo().getName();

        if (configManager.getDisabledServers().contains(currentServer)) {
            p.sendMessage(new ComponentBuilder(configManager.getDisabledServerMsg()).color(ChatColor.RED).create());
            return;
        }

        String hub = configManager.getHub();
        if (hub.equalsIgnoreCase("default")) {
            ListenerInfo listener = p.getPendingConnection().getListener();

            hub = listener.getFallbackServer();
        }

        if (currentServer.equals(hub)) {
            p.sendMessage(new ComponentBuilder(configManager.getAlreadyInHubMsg()).color(ChatColor.RED).create());
            return;
        }

        ServerInfo hubServer = main.getProxy().getServerInfo(hub);
        if (hubServer == null) {
            throw new RuntimeException("Cannot find server '" + hub + "'");
        }
        p.connect(hubServer);
    }
}
