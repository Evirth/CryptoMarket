package pl.csrv.divinecraft.evirth.cryptomarket.commands;

import pl.csrv.divinecraft.evirth.cryptomarket.commands.player.*;

public class CommandFactory {
    public static ICommand create(String command) {
        ICommand c = null;
        switch (command) {
            case "help":
                c = new HelpCommand();
                break;
            case "balance":
                c = new BalanceCommand();
                break;
            case "withdraw":
                c = new WithdrawCommand();
                break;
            case "deposit":
                c = new DepositCommand();
                break;
            case "transfer":
                c = new TransferCommand();
                break;
            case "exchange":
                c = new ExchangeCommand();
                break;
            case "price":
                c = new PriceCommand();
                break;
            case "global":
                c = new GlobalCommand();
                break;
        }
        return c;
    }
}
