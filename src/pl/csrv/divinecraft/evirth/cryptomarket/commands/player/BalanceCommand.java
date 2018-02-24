package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import pl.csrv.divinecraft.evirth.cryptomarket.api.Player;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;

public class BalanceCommand implements ICommand {
    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof HumanEntity) {
            Player p = new Player(commandSender.getName());
            p.checkBalance();
        } else {
            commandSender.sendMessage("[CryptoMarket] Only players can use this command.");
        }
        return true;
    }
}
