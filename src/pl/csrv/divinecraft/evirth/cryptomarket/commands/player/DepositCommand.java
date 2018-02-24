package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.Player;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.CommandExecutor;

public class DepositCommand extends CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof HumanEntity) {
            if (strings.length != 3) {
                return false;
            }

            try {
                Player p = new Player(commandSender.getName());
                int amount = Integer.parseInt(strings[1]);
                String crypto = strings[2];
                p.deposit(amount, crypto, null);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("CouldNotCompleteThisTransaction"));
            }
        } else {
            commandSender.sendMessage("[CryptoMarket] Only players can use this command.");
        }
        return true;
    }
}
