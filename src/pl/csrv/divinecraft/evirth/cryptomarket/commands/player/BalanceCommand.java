package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.api.Player;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.Permissions;

public class BalanceCommand implements ICommand {

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof HumanEntity) {
            if (!commandSender.hasPermission(Permissions.CRYPTOMARKET_PLAYER)) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
                return true;
            }

            Player p = new Player(commandSender.getName());
            p.printBalance();
        } else {
            commandSender.sendMessage("[CryptoMarket] Only players can use this command.");
        }
        return true;
    }
}
