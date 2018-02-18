package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.CommandExecutor;

public class HelpCommand extends CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();

        if (strings.length < 2) {
            String helpMsg = String.format("This is cryptocurrency market simulator based on real prices.\nOne Diamond is the equivalent of $%d.\n", CryptoMarket.config.price);
            sb.append(helpMsg);
            for (pl.csrv.divinecraft.evirth.cryptomarket.commands.models.Command c : AvailableCommands) {
                sb.append(String.format("/%s - %s\n", c.name, c.description));
            }
        }
        else {
            for (pl.csrv.divinecraft.evirth.cryptomarket.commands.models.Command c : AvailableCommands) {
                if (strings[1].equals(c.name)) {
                    sb.append(String.format("/cm %s - %s\nUsage: %s\n", c.name, c.description, c.usage));
                    break;
                }
            }
        }
        commandSender.sendMessage(sb.toString().split("\n"));
        return true;
    }
}
