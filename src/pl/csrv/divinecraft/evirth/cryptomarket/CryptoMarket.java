package pl.csrv.divinecraft.evirth.cryptomarket;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import pl.csrv.divinecraft.evirth.cryptomarket.commands.CommandExecutor;
import pl.csrv.divinecraft.evirth.cryptomarket.models.Config;

import java.io.File;

public class CryptoMarket extends JavaPlugin {
    public static Config config;

    @Override
    public void onEnable() {
        this.getCommand("cryptomarket").setExecutor(new CommandExecutor());
        InitializeConfig();
        getLogger().info("CryptoMarket enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("CryptoMarket disabled.");
    }

    private void InitializeConfig() {
        FileConfiguration fc = this.getConfig();
        fc.addDefault("price", 10);
        fc.options().copyDefaults(true);
        this.saveConfig();

        File dir = new File("./plugins/CryptoMarket/Players");
        if (!dir.exists()) {
            dir.mkdir();
        }

        config = new Config();
        config.price = fc.getInt("price");
    }
}
