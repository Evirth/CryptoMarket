package main.java.cryptomarket.commands.player;

import main.java.cryptomarket.CryptoMarket;
import main.java.cryptomarket.api.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import main.java.cryptomarket.commands.ICommand;
import main.java.cryptomarket.commands.Permissions;

public class ExchangeCommand implements ICommand {

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof HumanEntity) {
            if (!commandSender.hasPermission(Permissions.CRYPTOMARKET_PLAYER)) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
                return true;
            }

            if (strings.length != 4) {
                commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("IncorrectUseOfCommand"), strings[0]));
                return true;
            }

            try {
                Player p = new Player(commandSender.getName());
                double amount = strings[2].equalsIgnoreCase("all") ? Double.POSITIVE_INFINITY : Double.parseDouble(strings[2].replace(",", "."));
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
