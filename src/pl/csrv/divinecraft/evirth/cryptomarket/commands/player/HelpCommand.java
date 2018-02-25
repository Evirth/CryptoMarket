package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import org.bukkit.command.CommandSender;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.CommandExecutorImpl;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.models.Command;

public class HelpCommand implements ICommand {
    private String permission = "cryptomarket.player";

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        if (!commandSender.hasPermission(this.permission)) {
            commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
            return true;
        }

        if (strings == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();

        if (strings.length < 2) {
            String helpMsg = String.format(CryptoMarket.resourceManager.getResource("HelpHeader") + "\n", CryptoMarket.config.price);
            sb.append(helpMsg);
            for (Command c : CommandExecutorImpl.availableCommands) {
                sb.append(String.format("/cm %s - %s\n", c.name, c.description));
            }
        }
        else {
            for (Command c : CommandExecutorImpl.availableCommands) {
                if (strings[1].equals(c.name)) {
                    sb.append(String.format("/cm %s - %s\n%s: %s\n", c.name, c.description, CryptoMarket.resourceManager.getResource("Usage"),c.usage));
                    break;
                }
            }
        }
        commandSender.sendMessage(sb.toString().split("\n"));
        return true;
    }
}
