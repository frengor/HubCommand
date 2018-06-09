package com.fren_gor.hubcommand;

import java.io.File;
import java.io.IOException;

import net.md_5.bungee.BungeeCord;
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

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {

		if (!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(new TextComponent("You must be a player to do /hub"));
			return;
		}

		ProxiedPlayer p = (ProxiedPlayer) sender;

		String s = getHub();
		if (s.equalsIgnoreCase("default")) {
			ListenerInfo listener = p.getPendingConnection().getListener();

			s = listener.getFallbackServer();

		}

		if (p.getServer().getInfo().getName().equals(s)) {
			p.sendMessage(new ComponentBuilder(getError()).color(ChatColor.RED).create());
			return;
		}

		p.connect(BungeeCord.getInstance().getServerInfo(s));
	}

	private String getHub() {
		Configuration c = null;
		try {
			c = ConfigurationProvider.getProvider(YamlConfiguration.class)
					.load(new File(Main.getInstance().getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return c.getString("hub");

	}

	private String getError() {
		Configuration c = null;
		try {
			c = ConfigurationProvider.getProvider(YamlConfiguration.class)
					.load(new File(Main.getInstance().getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return c.getString("alreadyInHub");

	}

}
