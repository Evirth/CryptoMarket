package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import pl.csrv.divinecraft.evirth.cryptomarket.commands.CommandExecutor;

public class GlobalCommand extends CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("TODO: Market cap etc.");
        } catch (Exception e) {
            sb.append("Could not get information about crypto :(. Please, try again later.");
        }
        commandSender.sendMessage(sb.toString().split("\n"));
        return true;
    }
}
