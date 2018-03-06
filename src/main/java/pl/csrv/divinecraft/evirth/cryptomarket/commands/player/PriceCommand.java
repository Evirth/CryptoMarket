package main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import main.java.pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.CoinMarketCap;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.models.CoinMarket;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.Permissions;

import java.util.List;

public class PriceCommand implements ICommand {

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        if (!commandSender.hasPermission(Permissions.CRYPTOMARKET_PLAYER)) {
            commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
            return true;
        }

        StringBuilder sb = new StringBuilder();
        try {
            if (strings.length < 2) {
                List<CoinMarket> list = CoinMarketCap.ticker(10);
                if (list != null) {
                    for (CoinMarket coin : list) {
                        sb.append(ChatColor.translateAlternateColorCodes('&', String.format("#%s. &6%s&f (&6%s&f) - &6$%.6f&f\n", coin.getRank(), coin.getName(), coin.getSymbol(), coin.getPriceUSD())));
                    }
                }
            } else {
                CoinMarket coin = CoinMarketCap.ticker(strings[1]);
                sb.append(ChatColor.translateAlternateColorCodes('&', String.format("#%s. &6%s&f (&6%s&f) - &6$%.6f&f", coin.getRank(), coin.getName(), coin.getSymbol(), coin.getPriceUSD())));
            }
        } catch (Exception e) {
            sb.append(CryptoMarket.resourceManager.getResource("CouldNotGetInfoAboutCrypto"));
        }
        commandSender.sendMessage(sb.toString().split("\n"));
        return true;
    }
}
