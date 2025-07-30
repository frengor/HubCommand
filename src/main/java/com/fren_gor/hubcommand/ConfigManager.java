package com.fren_gor.hubcommand;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigManager {

    private static final int VERSION = 3;

    private final File configFile;
    private final Yaml configLoader = new Yaml();

    private Map<String, Object> config = new HashMap<>();
    private String hub, alreadyInHubMsg, disabledServerMsg;
    private final Set<String> disabledServers = new HashSet<>();

    public ConfigManager(File dataFolder) {
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        this.configFile = new File(dataFolder, "config.yml");
        try {
            copyConfig();
            reloadConfig();
            updateConfig();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load config.yml", e);
        }
    }

    public void reloadConfig() throws IOException {
        config = configLoader.load(Files.newInputStream(configFile.toPath()));
        hub = (String) config.get("hub");
        alreadyInHubMsg = (String) config.get("alreadyInHub");
        disabledServerMsg = (String) config.get("disabledServersError");
        disabledServers.clear();
        for (Object o : (List<?>) config.get("disabled-servers")) {
            disabledServers.add((String) o);
        }
    }

    public void copyConfig() throws IOException {
        if (!configFile.exists()) {
            try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("config.yml")) {
                if (in == null) {
                    throw new IOException("Couldn't find config.yml inside jar.");
                }
                Files.copy(in, configFile.toPath());
            }
        }
    }

    public void updateConfig() throws IOException {
        int ver = 1; // Default version 1
        if (config.containsKey("config-version")) {
            Object v = config.get("config-version");
            if (v instanceof Integer) {
                ver = (int) v;
            }
        }

        if (ver >= VERSION) {
            return;
        }

        Map<String, Object> newConfig;
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("config.yml")) {
            if (in == null) {
                throw new IOException("Couldn't find config.yml inside jar.");
            }
            newConfig = configLoader.load(in);
        }

        config.remove("config-version");
        for (String s : newConfig.keySet().toArray(new String[0])) {
            if (config.containsKey(s)) {
                newConfig.put(s, config.get(s));
            }
        }

        if (configFile.exists()) {
            configFile.delete();
        }
        try (FileWriter writer = new FileWriter(configFile)) {
            configLoader.dump(newConfig, writer);
        }
        reloadConfig();
    }

    public boolean needsPermission() {
        return (Boolean) config.get("requiresPermission");
    }

    public boolean hasDisabledServers() {
        return !disabledServers.isEmpty();
    }

    public String getHub() {
        return hub;
    }

    public String getAlreadyInHubMsg() {
        return alreadyInHubMsg;
    }

    public String getDisabledServerMsg() {
        return disabledServerMsg;
    }

    public Set<String> getDisabledServers() {
        return disabledServers;
    }
}
