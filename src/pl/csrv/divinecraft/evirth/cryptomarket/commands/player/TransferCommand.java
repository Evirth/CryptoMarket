package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.api.Player;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;

public class TransferCommand implements ICommand {
    private String permission = "cryptomarket.player";

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        if (!commandSender.hasPermission(permission)) {
            commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
            return true;
        }

        if (commandSender instanceof HumanEntity) {
            if (strings.length != 4) {
                commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("IncorrectUseOfCommand"), strings[0]));
                return true;
            }

            try {
                Player p = new Player(commandSender.getName());
                String inputAmount = strings[2].replace(",", ".");
                p.transfer(strings[1], inputAmount, strings[3]);
            } catch (Exception e) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("CouldNotCompleteThisTransaction"));
            }
        } else {
            commandSender.sendMessage("[CryptoMarket] Only players can use this command.");
        }
        return true;
    }
}
