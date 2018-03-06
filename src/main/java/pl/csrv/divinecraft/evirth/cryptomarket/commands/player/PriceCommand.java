package main.java.cryptomarket.commands.player;

import main.java.cryptomarket.CryptoMarket;
import main.java.cryptomarket.coinmarketcap.CoinMarketCap;
import main.java.cryptomarket.coinmarketcap.models.CoinMarket;
import org.bukkit.command.CommandSender;
import main.java.cryptomarket.commands.ICommand;
import main.java.cryptomarket.commands.Permissions;

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
                        sb.append(String.format("#%s. %s (%s) - %.6f USD\n", coin.getRank(), coin.getName(), coin.getSymbol(), coin.getPriceUSD()));
                    }
                }
            } else {
                CoinMarket coin = CoinMarketCap.ticker(strings[1]);
                sb.append(String.format("#%s. %s (%s) - %.6f USD", coin.getRank(), coin.getName(), coin.getSymbol(), coin.getPriceUSD()));
            }
        } catch (Exception e) {
            sb.append(CryptoMarket.resourceManager.getResource("CouldNotGetInfoAboutCrypto"));
        }
        commandSender.sendMessage(sb.toString().split("\n"));
        return true;
    }
}
