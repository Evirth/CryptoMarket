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
                new Command("withdraw", CryptoMarket.resourceManager.getResource("WithdrawCommandDescription"), "/cm withdraw <amount> <crypto>"),
                new Command("deposit", CryptoMarket.resourceManager.getResource("DepositCommandDescription"), "/cm deposit <amount> <crypto>"),
                new Command("transfer", CryptoMarket.resourceManager.getResource("TransferCommandDescription"), "/cm transfer <crypto> <amount> <toPlayer>"),
                new Command("exchange", CryptoMarket.resourceManager.getResource("ExchangeCommandDescription"), "/cm exchange <fromCrypto> <amount>[d] <toCrypto>"),
                new Command("price", CryptoMarket.resourceManager.getResource("PriceCommandDescription"), "/cm price [<crypto>]"),
                //new Command("global", CryptoMarket.resourceManager.getResource("GlobalCommandDescription"), "/cm global"),
                new Command("stats", CryptoMarket.resourceManager.getResource("StatsCommandDescription"), "/cm stats"),
                new Command("history", CryptoMarket.resourceManager.getResource("HistoryCommandDescription"), "/cm history [<page>]"),
                new Command("add", CryptoMarket.resourceManager.getResource("AddCommandDescription"), "/cm add <player> <amount>[d] <crypto>"),
                new Command("remove", CryptoMarket.resourceManager.getResource("RemoveCommandDescription"), "/cm remove <player> <amount>[d] <crypto>"),
                new Command("check", CryptoMarket.resourceManager.getResource("CheckCommandDescription"), "/cm check <player>")
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
