package main.java.cryptomarket.commands;

import org.bukkit.command.CommandSender;

public interface ICommand {
    boolean execute(CommandSender commandSender, String[] strings);
}
