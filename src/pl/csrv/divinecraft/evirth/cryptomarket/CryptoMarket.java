package pl.csrv.divinecraft.evirth.cryptomarket;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.CommandExecutor;
import pl.csrv.divinecraft.evirth.cryptomarket.models.Config;
import pl.csrv.divinecraft.evirth.cryptomarket.resources.ResourceManager;

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
        PluginCommand pc = this.getCommand("cryptomarket");
        pc.setExecutor(new CommandExecutor());
        this.getLogger().info("CryptoMarket enabled.");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("CryptoMarket disabled.");
    }

    private void initializeConfig() {
        FileConfiguration fc = this.getConfig();
        fc.addDefault("price", 10);
        fc.addDefault("lang", "en");
        fc.options().copyDefaults(true);
        this.saveConfig();

        config = new Config();
        config.price = fc.getInt("price");
        config.lang = fc.getString("lang");
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
