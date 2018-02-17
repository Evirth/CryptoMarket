package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import pl.csrv.divinecraft.evirth.cryptomarket.commands.helper.CommandHelper;

public class HelpCommand extends CommandHelper {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();

        if (strings.length < 2) {
            String helpMsg = "This is cryptocurrency market simulator based on real prices.\n";
            sb.append(helpMsg);
            for (pl.csrv.divinecraft.evirth.cryptomarket.commands.model.Command c : AvailableCommands) {
                sb.append(String.format("/%s - %s\n", c.name, c.description));
            }
        }
        else {
            for (pl.csrv.divinecraft.evirth.cryptomarket.commands.model.Command c : AvailableCommands) {
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
