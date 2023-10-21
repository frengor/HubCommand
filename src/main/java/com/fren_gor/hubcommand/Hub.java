package com.fren_gor.hubcommand;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Hub extends Command {

    public Hub() {
        super("hub");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("You must be a player to do /hub"));
            return;
        }

        Configuration c;
        try {
            c = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(Main.getInstance().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        ProxiedPlayer p = (ProxiedPlayer) sender;

        if (getDisabledServers(c).contains(p.getServer().getInfo().getName())) {
            p.sendMessage(new ComponentBuilder(getDisabledError(c)).color(ChatColor.RED).create());
            return;
        }

        String s = getHub(c);
        if (s.equalsIgnoreCase("default")) {
            ListenerInfo listener = p.getPendingConnection().getListener();

            s = listener.getFallbackServer();

        }

        if (p.getServer().getInfo().getName().equals(s)) {
            p.sendMessage(new ComponentBuilder(getError(c)).color(ChatColor.RED).create());
            return;
        }

        p.connect(Main.getInstance().getProxy().getServerInfo(s));
    }

    private String getHub(Configuration c) {
        return c.getString("hub");
    }

    private String getError(Configuration c) {
        return c.getString("alreadyInHub");
    }

    private String getDisabledError(Configuration c) {
        return c.getString("disabledServersError");
    }

    private List<String> getDisabledServers(Configuration c) {
        return c.getStringList("disabled-servers");
    }
}
