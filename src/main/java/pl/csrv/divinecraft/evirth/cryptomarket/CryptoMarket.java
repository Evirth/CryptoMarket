package main.java.pl.csrv.divinecraft.evirth.cryptomarket;

import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.CommandExecutorImpl;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.models.Config;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.plugin.java.JavaPlugin;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.resources.ResourceManager;

import java.io.*;
import java.nio.file.Paths;

public class CryptoMarket extends JavaPlugin {
    public static Config config;
    public static ResourceManager resourceManager;
    public static String pluginDir;

    @Override
    public void onEnable() {
        pluginDir = Paths.get("plugins", CryptoMarket.class.getSimpleName()).toString();
        this.createFolders();
        this.initializeConfig();
        resourceManager = new ResourceManager(this);
        this.getCommand("cryptomarket").setExecutor(new CommandExecutorImpl());
        this.getLogger().info("CryptoMarket enabled.");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("CryptoMarket disabled.");
    }

    private void initializeConfig() {
        this.saveDefaultConfig();
        FileConfiguration fc = this.getConfig();

        config = new Config();
        config.setPrice(fc.getInt("price"));
        config.setLang(fc.getString("lang"));
        config.setFee(fc.getDouble("fee") / 100);
    }

    private void createFolders() {
        File data = new File(Paths.get(pluginDir, "Players").toString());
        if (!data.exists()) {
            data.mkdirs();
        }

        File resources = new File(Paths.get(pluginDir, "Resources").toString());
        if (!resources.exists()) {
            resources.mkdirs();
        }
    }
}
