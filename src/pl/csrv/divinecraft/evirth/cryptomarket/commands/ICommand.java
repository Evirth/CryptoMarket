package pl.csrv.divinecraft.evirth.cryptomarket.commands;

import org.bukkit.command.CommandSender;

public interface ICommand {
    boolean execute(CommandSender commandSender, String[] strings);
}
