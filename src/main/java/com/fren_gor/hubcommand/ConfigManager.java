package com.fren_gor.hubcommand;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigManager {

    private static final int version = 2;

    public static void updateVersion() {
        Configuration c;
        try {
            c = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(Main.getInstance().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int v = 1;

        if (c.contains("config-version")) {
            v = c.getInt("config-version");
        }

        if (v >= version) {
            return;
        }

        Map<String, Object> m = new HashMap<>();

        for (String s : c.getKeys()) {
            if (!s.equals("config-version")) {
                m.put(s, c.get(s));
            }
        }

        new File(Main.getInstance().getDataFolder(), "config.yml").delete();

        try (InputStream in = Main.getInstance().getResourceAsStream("config.yml")) {
            Files.copy(in, new File(Main.getInstance().getDataFolder(), "config.yml").toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            c = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(Main.getInstance().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (String s : c.getKeys()) {
            if (m.containsKey(s)) {
                c.set(s, m.get(s));
            }
        }

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .save(c, new File(Main.getInstance().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
