package pl.csrv.divinecraft.evirth.cryptomarket.commands;

import org.bukkit.command.CommandSender;

import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.models.Command;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.player.*;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {
    public Command[] AvailableCommands;

    public CommandExecutor() {
        AvailableCommands = new Command[]{
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
                new GlobalCommand().onCommand(commandSender, command, s, strings);
                break;
        }
        return true;
    }
}
