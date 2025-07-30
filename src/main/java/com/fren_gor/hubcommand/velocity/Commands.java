package com.fren_gor.hubcommand.velocity;

import com.fren_gor.hubcommand.ConfigManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public class Commands {

    public static BrigadierCommand getCommand(final MainVelocity main) {
        LiteralArgumentBuilder<CommandSource> cmd = BrigadierCommand.literalArgumentBuilder("hub");
        if (main.getConfigManager().needsPermission()) {
            cmd = cmd.requires(source -> source.hasPermission("hubcommand.command"));
        }
        final ConfigManager configManager = main.getConfigManager();
        cmd = cmd.executes(context -> {
            if (!(context.getSource() instanceof Player)) {
                context.getSource().sendMessage(Component.text("You must be a player to do /hub", NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }

            Player p = (Player) context.getSource();
            String currentServer = p.getCurrentServer().map(s -> s.getServerInfo().getName()).orElse("");

            if (configManager.getDisabledServers().contains(currentServer)) {
                p.sendMessage(Component.text(configManager.getDisabledServerMsg(), NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }

            String hub = configManager.getHub();
            if (hub.equalsIgnoreCase("default")) {
                Collection<String> servers = main.getProxyServer().getConfiguration().getAttemptConnectionOrder();
                if (servers == null || servers.isEmpty()) {
                    servers = main.getProxyServer().getConfiguration().getServers().keySet();
                }
                Iterator<String> it = servers.iterator();
                if (it.hasNext()) {
                    hub = it.next();
                } else {
                    throw new RuntimeException("Cannot find default server");
                }
            }

            if (currentServer.equals(hub)) {
                p.sendMessage(Component.text(configManager.getAlreadyInHubMsg(), NamedTextColor.RED));
                return Command.SINGLE_SUCCESS;
            }

            Optional<RegisteredServer> hubServer = main.getProxyServer().getServer(hub);
            if (!hubServer.isPresent()) {
                throw new RuntimeException("Cannot find server '" + hub + "'");
            }
            p.createConnectionRequest(hubServer.get()).fireAndForget();
            return Command.SINGLE_SUCCESS;
        });

        return new BrigadierCommand(cmd);
    }
}
