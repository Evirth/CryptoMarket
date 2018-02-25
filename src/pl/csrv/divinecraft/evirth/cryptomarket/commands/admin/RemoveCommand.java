package pl.csrv.divinecraft.evirth.cryptomarket.commands.admin;

import org.bukkit.command.CommandSender;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;

public class RemoveCommand implements ICommand {
    private String permission = "cryptomarket.admin";

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        if (!commandSender.hasPermission(permission)) {
            commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
            return true;
        }

        return true;
    }
}
