package main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import main.java.pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.CoinMarketCap;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.models.Global;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;

public class GlobalCommand implements ICommand {

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        if (!commandSender.hasPermission(Permissions.CRYPTOMARKET_PLAYER) && !commandSender.hasPermission(Permissions.CRYPTOMARKET_ADMIN)) {
            commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
            return true;
        }

        try {
            StringBuilder sb = new StringBuilder();
            Global global = CoinMarketCap.global();
            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(global.getLastUpdated());
            sb.append(ChatColor.translateAlternateColorCodes('&', "&6-------------- Global Data -------------\n"));
            sb.append(String.format("Market Cap: $%,.0f\n", global.getTotalMarketCapUSD()));
            sb.append(String.format("24h Volume: $%,.0f\n", global.getTotal24hVolumeUSD()));
            sb.append(String.format("BTC Dominance: %.2f%%\n", global.getBitcoinPercentageOfMarketCap()));
            sb.append(String.format("Updated: %s\n", date));
            sb.append(ChatColor.translateAlternateColorCodes('&', "&6--------------------------------------\n"));
            commandSender.sendMessage(sb.toString().split("\n"));
        } catch (Exception e) {
            commandSender.sendMessage(CryptoMarket.resourceManager.getResource("CouldNotGetInfoAboutGlobals"));
        }

        return true;
    }
}
