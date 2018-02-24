package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.api.Player;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;

public class ExchangeCommand implements ICommand {
    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof HumanEntity) {
            if (strings.length != 4) {
                commandSender.sendMessage("");
                return false;
            }

            try {
                double amount = Double.parseDouble(strings[2].replace(",", "."));
                Player p = new Player(commandSender.getName());
                p.exchange(strings[1], amount, strings[3]);
            } catch (Exception e) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("CouldNotCompleteThisTransaction"));
            }
        } else {
            commandSender.sendMessage("[CryptoMarket] Only players can use this command.");
        }
        return true;
    }
}
