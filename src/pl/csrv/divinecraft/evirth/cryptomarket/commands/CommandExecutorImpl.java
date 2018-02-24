package pl.csrv.divinecraft.evirth.cryptomarket.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.models.Command;

public class CommandExecutorImpl implements CommandExecutor {
    public static Command[] availableCommands;

    public CommandExecutorImpl() {
        availableCommands = new Command[]{
                new Command("help", CryptoMarket.resourceManager.getResource("HelpCommandDescription")),
                new Command("balance", CryptoMarket.resourceManager.getResource("BalanceCommandDescription")),
                new Command("withdraw", CryptoMarket.resourceManager.getResource("WithdrawCommandDescription"), "/cm withdraw 10 Bitcoin"),
                new Command("deposit", CryptoMarket.resourceManager.getResource("DepositCommandDescription"), "/cm deposit 10 Bitcoin"),
                new Command("transfer", CryptoMarket.resourceManager.getResource("TransferCommandDescription"), "'/cm transfer Bitcoin 0.01 Evirth' or '/cm transfer Bitcoin 1D Evirth'"),
                new Command("exchange", CryptoMarket.resourceManager.getResource("ExchangeCommandDescription"), "'/cm exchange Bitcoin 1 IOTA' or '/cm exchange Bitcoin 1D IOTA'"),
                new Command("price", CryptoMarket.resourceManager.getResource("PriceCommandDescription"), "'/cm price IOTA' or '/cm price' - top 10"),
                new Command("global", CryptoMarket.resourceManager.getResource("GlobalCommandDescription"), "/cm global"),
                new Command("stats", CryptoMarket.resourceManager.getResource("StatsCommandDescription"), "/cm stats")
        };
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        String cmd = "help";
        if (strings != null && strings.length > 0) {
            cmd = strings[0];
        }

        ICommand c = CommandFactory.create(cmd);
        return c.execute(commandSender, strings);
    }
}
