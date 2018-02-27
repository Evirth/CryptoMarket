package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import com.lucadev.coinmarketcap.CoinMarketCap;
import com.lucadev.coinmarketcap.model.CoinMarket;
import org.bukkit.command.CommandSender;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.Permissions;

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
                List<CoinMarket> list = CoinMarketCap.ticker().setLimit(10).get().getMarkets();
                if (list != null) {
                    for (CoinMarket coin : list) {
                        sb.append(String.format("#%s. %s (%s) - %.6f USD\n", coin.getRank(), coin.getName(), coin.getSymbol(), coin.getPriceUSD()));
                    }
                }
            } else {
                CoinMarket coin = CoinMarketCap.ticker(strings[1]).get();
                sb.append(String.format("#%s. %s (%s) - %.6f USD", coin.getRank(), coin.getName(), coin.getSymbol(), coin.getPriceUSD()));
            }
        } catch (Exception e) {
            sb.append(CryptoMarket.resourceManager.getResource("CouldNotGetInfoAboutCrypto"));
        }
        commandSender.sendMessage(sb.toString().split("\n"));
        return true;
    }
}
