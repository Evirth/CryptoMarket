package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import com.lucadev.coinmarketcap.CoinMarketCap;
import com.lucadev.coinmarketcap.model.CoinMarket;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import pl.csrv.divinecraft.evirth.cryptomarket.commands.CommandExecutor;

import java.util.List;

public class PriceCommand extends CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
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
            sb.append("Could not get information about crypto :(. Please, try again later.");
        }
        commandSender.sendMessage(sb.toString().split("\n"));
        return true;
    }
}
