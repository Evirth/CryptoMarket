package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.api.Player;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;

public class WithdrawCommand implements ICommand {
    private String permission = "cryptomarket.player";

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        if (!commandSender.hasPermission(permission)) {
            commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
            return true;
        }

        if (commandSender instanceof HumanEntity) {
            try {
                if (strings.length == 2 && strings[1].equals("all")) {
                    Player p = new Player(commandSender.getName());
                    p.withdrawAll();
                    return true;
                }

                if (strings.length != 3) {
                    commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("IncorrectUseOfCommand"), strings[0]));
                    return true;
                }

                Player p = new Player(commandSender.getName());
                int amount = Integer.parseInt(strings[1]);
                String crypto = strings[2];
                p.withdraw(amount, crypto);
            } catch (Exception e) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("CouldNotCompleteThisTransaction"));
            }
        } else {
            commandSender.sendMessage("[CryptoMarket] Only players can use this command.");
        }
        return true;
    }
}
