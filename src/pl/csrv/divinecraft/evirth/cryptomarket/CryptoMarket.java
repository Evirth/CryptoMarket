package pl.csrv.divinecraft.evirth.cryptomarket;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import pl.csrv.divinecraft.evirth.cryptomarket.commands.helper.CommandHelper;

public class CryptoMarket extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getCommand("cryptomarket").setExecutor(new CommandHelper());
        getLogger().info("CryptoMarket enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("CryptoMarket disabled.");
    }
}
