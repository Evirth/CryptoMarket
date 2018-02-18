package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import pl.csrv.divinecraft.evirth.cryptomarket.Player;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.CommandExecutor;

public class BalanceCommand extends CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof HumanEntity) {
            Player p = new Player(commandSender.getName());
            p.checkBalance();
        } else {
            commandSender.sendMessage("[CryptoMarket] Only players can use this command.");
        }
        return true;
    }
}
