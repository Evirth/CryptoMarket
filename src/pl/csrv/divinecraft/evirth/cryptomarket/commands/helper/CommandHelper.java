package pl.csrv.divinecraft.evirth.cryptomarket.commands.helper;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import pl.csrv.divinecraft.evirth.cryptomarket.commands.model.Command;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.player.*;

public class CommandHelper implements CommandExecutor {

    protected static Command[] AvailableCommands = new Command[] {
            new Command("help", "Gets information about the CryptoMarket plugin."),
            new Command("balance", "Shows current balance."),
            new Command("withdraw", "Exchanges cryptocurrencies to Diamonds.", "/cm withdraw BTC 1"),
            new Command("deposit", "Exchanges Diamonds to cryptocurrencies.", "/cm deposit 10"),
            new Command("transfer", "Transfers cryptocurrencies to another player's account.", "/cm transfer BTC 1 Evirth"),
            new Command("exchange", "Exchanges cryptocurrency to another one.", "/cm exchange BTC 1 IOTA"),
            new Command("price", "Shows the current price of cryptocurrency.", "/cm price IOTA"),
            new Command("global", "Gets information about the market cap.", "/cm global")
    };

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        String cmd = "help";
        if (strings != null && strings.length > 0) {
            cmd = strings[0];
        }

        switch (cmd) {
            case "help":
                new HelpCommand().onCommand(commandSender, command, s, strings);
                break;
            case "balance":
                new BalanceCommand().onCommand(commandSender, command, s, strings);
                break;
            case "withdraw":
                new WithdrawCommand().onCommand(commandSender, command, s, strings);
                break;
            case "deposit":
                new DepositCommand().onCommand(commandSender, command, s, strings);
                break;
            case "transfer":
                new TransferCommand().onCommand(commandSender, command, s, strings);
                break;
            case "exchange":
                new ExchangeCommand().onCommand(commandSender, command, s, strings);
                break;
            case "price":
                new PriceCommand().onCommand(commandSender, command, s, strings);
                break;
            case "global":
                new PriceCommand().onCommand(commandSender, command, s, strings);
                break;
        }
        return true;
    }
}
