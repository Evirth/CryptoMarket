package pl.csrv.divinecraft.evirth.cryptomarket;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import pl.csrv.divinecraft.evirth.cryptomarket.commands.helper.CommandHelper;

public class CryptoMarket extends JavaPlugin {
    public static FileConfiguration Config;

    @Override
    public void onEnable() {
        this.getCommand("cryptomarket").setExecutor(new CommandHelper());
        Config = this.getConfig();
        Config.addDefault("price", 10);
        Config.options().copyDefaults(true);
        this.saveConfig();
        getLogger().info("CryptoMarket enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("CryptoMarket disabled.");
    }
}
